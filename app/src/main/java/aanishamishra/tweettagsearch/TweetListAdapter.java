package aanishamishra.tweettagsearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aanisha
 */

class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.ViewHolder> {

    private ArrayList<HashMap<String,String>> tweets;

    public TweetListAdapter(ArrayList<HashMap<String,String>> tweets) {
        this.tweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_tweet, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = tweets.get(position).get(Constants.PARAM_NAME) + "("+tweets.get(position).get(Constants.PARAM_SCREN_NAME)+")";
        holder.heading.setText(name);
        holder.content.setText(tweets.get(position).get(Constants.PARAM_TEXT));
        holder.date.setText(tweets.get(position).get(Constants.PARAM_CREATED_AT));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView heading, content, date;
        public ViewHolder(View itemView) {
            super(itemView);
            heading = (TextView)itemView.findViewById(R.id.head);
            content = (TextView)itemView.findViewById(R.id.content);
            date = (TextView)itemView.findViewById(R.id.date);
        }
    }
}
