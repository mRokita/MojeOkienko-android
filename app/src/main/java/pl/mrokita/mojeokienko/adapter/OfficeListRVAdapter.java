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
import pl.mrokita.mojeokienko.activity.OfficeInfo;

/**
 * Created by mrokita on 19.12.15.
 */
public class OfficeListRVAdapter extends RecyclerView.Adapter<OfficeListRVAdapter.CustomViewHolder>{
    private Context mContext;
    private List<Api.Office> mOffices = null;
    private boolean isLocked = false;
    public OfficeListRVAdapter(Context context){
        mContext = context;
    }

    public void setOffices(List<Api.Office> offices){
        isLocked = true;
        mOffices = offices;
        isLocked = false;
        notifyDataSetChanged();
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.office_list_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    private class OnClickIntent implements View.OnClickListener {
        private Intent mIntent;
        public OnClickIntent (Intent intent){
            this.mIntent = intent;
        }
        @Override
        public void onClick(View v) {
            mContext.startActivity(mIntent);
        }
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Api.Office office = mOffices.get(i);
        customViewHolder.name.setText(office.getName());
        Intent intent = new Intent(mContext, OfficeInfo.class);
        intent.putExtra("office", mOffices.get(i));
        customViewHolder.root.setOnClickListener(new OnClickIntent(intent));

    }

    @Override
    public int getItemCount() {
        if(isLocked) return 0;
        return mOffices.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected View root;
        public CustomViewHolder(View view) {
            super(view);
            this.root = view;
            this.name = (TextView) view.findViewById(R.id.txt_name);
        }
    }
}
