package aanishamishra.tweettagsearch;

import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Pair<String,String>> headers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String auth=Constants.CONSUMER_KEY + ":"+Constants.SECRET_KEY;
        String basicAuth = "Basic " + new String(Base64.encode(auth.getBytes(),0));
        headers.add(new Pair<>("Content_Type","application/x-www-form-urlencoded"));
        headers.add(new Pair<>("Authorization","Basic "+ basicAuth));
        NetworkRequest nr = new NetworkRequest(headers,"POST");
        nr.execute(Constants.OAUTH_URL);
    }
}
