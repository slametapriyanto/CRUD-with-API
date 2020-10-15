package apri.dts.crud_api;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    private String apiPath = "http://192.168.43.201/api-android/tampilsemuapgw.php";
    private ProgressDialog processDialog;
    private JSONArray rsJsonArray;
    private int success = 0;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.listview);
        new ApiGetData(this).execute();

        fabAdd = findViewById(R.id.button_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddDataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new ApiGetData(this).execute();
    }

    private class ApiGetData extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        String responseString = "";

        public ApiGetData(Context context){
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
            Request request = new Request.Builder().url(apiPath).build();
            try {
                success = 1;
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
                JSONObject resultJsonObject = new JSONObject(responseString);
                rsJsonArray = resultJsonObject.getJSONArray("result");

            }catch (IOException e){
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
                    for (int i = 0; i < rsJsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = rsJsonArray.getJSONObject(i);
                            Map<String, Object> itemMap = new HashMap<String, Object>();

                            itemMap.put("id", jsonObject.get("id"));
                            itemMap.put("name", jsonObject.get("name"));
                            itemMap.put("position", jsonObject.get("position"));
                            dataList.add(itemMap);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    SimpleAdapter adapter = new SimpleAdapter(mContext, dataList, android.R.layout.simple_list_item_2,
                            new String[]{"name", "position"}, new int[]{android.R.id.text1, android.R.id.text2});
                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Map<String, Object> item = dataList.get(position);
                            Intent intent = new Intent(MainActivity.this, EditDataActivity.class);
                            intent.putExtra("ext_id", item.get("id")+"");
                            startActivity(intent);
                            Log.d("LOG_TAG", "Click Id: " + item.get("id"));
                            Log.d("LOG_TAG", "Click Name: " + item.get("name"));
                        }
                    });
                }
            }
        }
    }
}