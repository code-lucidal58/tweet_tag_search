package aanishamishra.tweettagsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayList<Pair<String, String>> headers;
    ProgressDialog pd;
    RecyclerView tweetList;
    String result = "";
    SharedPreferences sharedPreferences;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading data");
        textView = (TextView) findViewById(R.id.text);
        tweetList = (RecyclerView) findViewById(R.id.tweets);
        sharedPreferences = getSharedPreferences(Constants.PREF_TOKEN,MODE_PRIVATE);
        String auth = null;
        try {
            auth = URLEncoder.encode(Constants.CONSUMER_KEY, "utf-8") + ":" + URLEncoder.encode(Constants.SECRET_KEY, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String basicAuth = "Basic " + new String(Base64.encode(auth.getBytes(), Base64.NO_WRAP));
        headers = new ArrayList<>();
        headers.add(new Pair<>("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"));
        headers.add(new Pair<>("Authorization", basicAuth));
        if(checkNetworkStatus()){
            networkrequest("POST", Constants.OAUTH_URL).execute(Constants.OAUTH_URL);

        }
    }

    public AsyncTask<String, Void, String> networkrequest(final String method, final String url) {
        return new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                HttpsURLConnection urlConnection = null;
                try {
                    URL url = new URL(strings[0]);

                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(method);
                    for (int i = 0; i < headers.size(); i++) {
                        urlConnection.setRequestProperty(headers.get(i).first, headers.get(i).second);
                    }

                    urlConnection.setReadTimeout(15000 /* milliseconds */);
                    urlConnection.setConnectTimeout(15000 /* milliseconds */);
                    if (strings[0].equals(Constants.OAUTH_URL)) {
                        List<Pair<String, String>> params = new ArrayList<>();
                        params.add(new Pair<>("grant_type", "client_credentials"));
                        OutputStream os = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(params));
                        writer.flush();
                        writer.close();
                        os.close();
                    }
                    urlConnection.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String jsonString;
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    jsonString = sb.toString();
                    Log.e("response", jsonString);

                    result = jsonString;
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getBaseContext(),"Oops! Something went wrong. Please try again",Toast.LENGTH_LONG).show();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                pd.dismiss();
                if(url.equals(Constants.OAUTH_URL)){
                    try {
                        JSONObject jsonObject= new JSONObject(s);
                        String token = jsonObject.optString("access_token");
                        sharedPreferences.edit().putString(Constants.ACCESS_TOKEN,token).apply();
                        Log.e("token",token);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Toast.makeText(getBaseContext(),"Oops! Something went wrong. Please try again",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    textView.setText(s);
                }
            }

            private String getQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException {
                StringBuilder result = new StringBuilder();
                boolean first = true;

                for (Pair<String, String> pair : params) {
                    if (first)
                        first = false;
                    else
                        result.append("&");

                    result.append(URLEncoder.encode(pair.first, "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(pair.second, "UTF-8"));
                }

                return result.toString();
            }
        };
    }

    private boolean checkNetworkStatus(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        headers = new ArrayList<>();
        headers.add(new Pair<>(Constants.AUTHORIZATION,"Bearer "+getSharedPreferences(Constants.PREF_TOKEN,MODE_PRIVATE)
                .getString(Constants.ACCESS_TOKEN,"")));
        networkrequest("GET",Constants.TWITTER_SEARCH_URL).execute(Constants.TWITTER_SEARCH_URL+"?q=%23"+query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
