package aanishamishra.tweettagsearch;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aanisha
 */

public class NetworkRequest extends AsyncTask<String, Void, Void> {
    public String result = "";
    private ArrayList<Pair<String,String>> headers;
    private String method="";

    public NetworkRequest(ArrayList<Pair<String, String>> headers, String method) {
        this.headers = headers;
        this.method = method;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(strings[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            for(int i=0;i<headers.size();i++){
                urlConnection.setRequestProperty(headers.get(i).first, headers.get(i).second);
            }
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String jsonString;
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            jsonString = sb.toString();
            Log.e("response", jsonString);

            result =jsonString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
