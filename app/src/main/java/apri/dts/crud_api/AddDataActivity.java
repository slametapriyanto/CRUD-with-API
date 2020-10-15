package apri.dts.crud_api;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddDataActivity extends AppCompatActivity {
    Button btnAdd;
    TextInputEditText inName, inDept, inSly;
    private String apiPath = "http://192.168.43.201/api-android/tambahpgw.php";
    private ProgressDialog processDialog;
    private int success = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        inName = findViewById(R.id.in_name);
        inDept = findViewById(R.id.in_dept);
        inSly = findViewById(R.id.in_sly);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> input = new HashMap<>();
                input.put("name", inName.getText().toString());
                input.put("position", inDept.getText().toString());
                input.put("salary", inSly.getText().toString());

                new ApiPostData(AddDataActivity.this, input).execute();
            }
        });
    }

    private class ApiPostData extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        String responseString = "";
        Map<String, String> mInput;

        public ApiPostData(Context context, Map<String, String> input){
            mContext = context;
            mInput = input;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage(getResources().getString(R.string.loading_post));
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", mInput.get("name"))
                    .addFormDataPart("position", mInput.get("position"))
                    .addFormDataPart("salary", mInput.get("salary"))
                    .build();

            Request request = new Request.Builder()
                    .url(apiPath)
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);
            try {
                responseString = call.execute().body().string();
                success = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (success == 1) {
                Toast.makeText(mContext, responseString,Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}