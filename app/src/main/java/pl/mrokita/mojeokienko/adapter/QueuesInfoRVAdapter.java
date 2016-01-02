package pl.mrokita.mojeokienko.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;

/**
 * Created by mrokita on 19.12.15.
 */
public class QueuesInfoRVAdapter extends RecyclerView.Adapter<QueuesInfoRVAdapter.CustomViewHolder>{
    private Context mContext;
    private List<Api.WindowQueue> mWindowQueues=null;
    public QueuesInfoRVAdapter(Context context){
        mContext = context;
    }

    public void setWindowQueues(List<Api.WindowQueue> windowQueues){
        mWindowQueues = windowQueues;
        notifyDataSetChanged();
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.queue_list_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Api.WindowQueue windowQueue = mWindowQueues.get(i);
        customViewHolder.letter.setText(windowQueue.getWindowLetter());
        customViewHolder.title.setText(windowQueue.getWindowName());
        customViewHolder.length.setText(windowQueue.getClientsInQueue().toString());
    }

    @Override
    public int getItemCount() {
        return mWindowQueues==null ? 0: mWindowQueues.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView letter;
        protected TextView title;
        protected TextView length;
        public CustomViewHolder(View view) {
            super(view);
            this.letter = (TextView) view.findViewById(R.id.txt_letter);
            this.length = (TextView) view.findViewById(R.id.txt_length);
            this.title = (TextView) view.findViewById(R.id.txt_title);
        }
    }
}
