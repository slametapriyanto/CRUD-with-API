package apri.dts.crud_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditDataActivity extends AppCompatActivity {
    private String apiPathGet;
    private String apiPathPost = "http://192.168.43.201/api-android/updatepgw.php";
    private String apiPathDelete;

    private ProgressDialog processDialog;
    private JSONArray rsJsonArray;
    private int success = 0;
    TextInputEditText inName, inDept, inSly;
    Button btnEdit, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        Bundle b = getIntent().getExtras();
        String extId = b.getString("ext_id");

        apiPathGet = "http://192.168.43.201/api-android/tampilpgw.php?id=" + extId;
        apiPathDelete = "http://192.168.43.201/api-android/hapuspgw.php?id=" + extId;

        inName = findViewById(R.id.in_name);
        inDept = findViewById(R.id.in_dept);
        inSly = findViewById(R.id.in_sly);
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> input = new HashMap<>();
                input.put("id", extId);
                input.put("name", inName.getText().toString());
                input.put("position", inDept.getText().toString());
                input.put("salary", inSly.getText().toString());

                new ApiPostData(EditDataActivity.this, input).execute();
            }
        });
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogKonfirmasiHapusCatatan();
            }
        });
        new ApiGetData(this).execute();
    }

    private class ApiGetData extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        String responseString = "";

        public ApiGetData(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage(getResources().getString(R.string.loading_info));
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiPathGet).build();
            try {
                success = 1;
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
                JSONObject resultJsonObject = new JSONObject(responseString);
                rsJsonArray = resultJsonObject.getJSONArray("result");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                if (null != rsJsonArray) {
                    final ArrayList<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

                    try {
                        JSONObject jsonObject = rsJsonArray.getJSONObject(0);
                        inName.setText(jsonObject.get("name").toString());
                        inDept.setText(jsonObject.get("position").toString());
                        inSly.setText(jsonObject.get("salary").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
                    .addFormDataPart("id", mInput.get("id"))
                    .addFormDataPart("name", mInput.get("name"))
                    .addFormDataPart("position", mInput.get("position"))
                    .addFormDataPart("salary", mInput.get("salary"))
                    .build();

            Request request = new Request.Builder()
                    .url(apiPathPost)
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

    private class ApiDelete extends AsyncTask<Void, Void, Void>{
        private Context mContext;
        String responseString = "";
        Map<String, String> mInput;

        public ApiDelete(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage(getResources().getString(R.string.loading_info));
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiPathDelete).build();
            try {
                success = 1;
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
                JSONObject resultJsonObject = new JSONObject(responseString);
                rsJsonArray = resultJsonObject.getJSONArray("result");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
    void tampilkanDialogKonfirmasiHapusCatatan (){
        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun ini ?")
                .setMessage("Apakah anda yakin ingin menghapus akun ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new ApiDelete(EditDataActivity.this).execute();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

}