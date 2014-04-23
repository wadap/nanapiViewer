package jp.wadap.nanapiviewer.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    static String api = "http://api.nanapi.jp/v1/recipeSearchDetails/?key=4b542e23e43f6&format=json";
    Context mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateContent(View view) {
        if (isConnected(this)) {
            Task task = new Task();
            task.execute(api);
        }
    }

    protected boolean isConnected(Context context){
        ConnectivityManager connectivityManger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManger.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return (networkInfo.isConnected());
    }

    protected class Task extends AsyncTask<String, String , String> {

        @Override
        protected String doInBackground(String... params){
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            byte[] result = null;
            String str = "";

            try {
                HttpResponse response = client.execute(get);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");
                }

            } catch (Exception e){
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONObject json = new JSONObject(result);

                mActivity = MainActivity.this;
                ArrayList<String> list = new ArrayList<String>();

                JSONArray jsonArray = json.getJSONObject("response").getJSONArray("recipes");

                for (int i = 0; i < jsonArray.length(); i++) {
                    String title = jsonArray.getJSONObject(i).getString("title");
                    list.add(title);
                }

                ListView listView = (ListView)findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);

                // on click
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        String str = (String) parent.getItemAtPosition(position);
                                                        Toast.makeText(mActivity, str, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                );

            } catch (JSONException e) {
            }
        }
    }
}
