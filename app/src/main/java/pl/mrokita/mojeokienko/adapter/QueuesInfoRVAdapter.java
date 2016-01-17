package pl.mrokita.mojeokienko.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.activity.NewTicket;

/**
 * Created by mrokita on 19.12.15.
 */
public class QueuesInfoRVAdapter extends RecyclerView.Adapter<QueuesInfoRVAdapter.CustomViewHolder>{
    private Context mContext;
    private boolean isLocked = false;
    private List<Api.WindowQueue> mWindowQueues=null;
    private Api.Office mOffice;
    public QueuesInfoRVAdapter(Context context, Api.Office office){
        mOffice = office;
        mContext = context;
    }

    public void setWindowQueues(List<Api.WindowQueue> windowQueues){
        isLocked = true;
        mWindowQueues = windowQueues;
        isLocked = false;
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                mContext).inflate(R.layout.queue_list_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final Api.WindowQueue windowQueue = mWindowQueues.get(i);
        customViewHolder.letter.setText(windowQueue.getWindowLetter());
        customViewHolder.title.setText(windowQueue.getWindowName());
        customViewHolder.length.setText(String.format("Liczba osób w kolejce: %d\nAktualny numer: %d",
                windowQueue.getClientsInQueue(), windowQueue.getCurrentNumber()));
        customViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewTicket.class);
                intent.putExtra("office", mOffice);
                intent.putExtra("windowQueue", windowQueue);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mWindowQueues==null || isLocked) ? 0: mWindowQueues.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView letter;
        protected TextView title;
        protected TextView length;
        protected View root;
        public CustomViewHolder(View view) {
            super(view);
            this.root = view;
            this.letter = (TextView) view.findViewById(R.id.txt_letter);
            this.length = (TextView) view.findViewById(R.id.txt_length);
            this.title = (TextView) view.findViewById(R.id.txt_title);
        }
    }
}
