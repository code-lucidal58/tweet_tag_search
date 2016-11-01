package aanishamishra.tweettagsearch;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(false);
            urlConnection.setDoInput(true);
            List<Pair<String,String>> params = new ArrayList<>();
            params.add(new Pair<>("grant_type", "client_credentials"));

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
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

    private String getQuery(List<Pair<String,String>> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String,String> pair : params)
        {
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
}
