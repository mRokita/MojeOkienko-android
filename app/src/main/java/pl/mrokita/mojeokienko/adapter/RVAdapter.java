package pl.mrokita.mojeokienko.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;

public class RVAdapter  extends RecyclerView.Adapter<RVAdapter.CustomViewHolder> {
    private Api.Office mOffice;
    private Context mContext;
    private int[] imgResources = {R.drawable.ic_phone_black_36dp,
                                  R.drawable.ic_map_marker_black_36dp,
                                  R.drawable.ic_earth_black_36dp,
                                  R.drawable.ic_clock_black_36dp,
                                  R.drawable.ic_account_multiple_black_36dp};

    private int[] labelResouces = {R.string.label_telefon,
                                   R.string.label_adres,
                                   R.string.label_www,
                                   R.string.label_open_hours,
                                   R.string.label_queues};

    public RVAdapter(Context context, Api.Office office) {
        this.mOffice = office;
        this.mContext = context;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView content;
        protected TextView label;
        protected LinearLayout container;
        protected LinearLayout textContainer;
        public CustomViewHolder(View view) {
            super(view);
            this.container = (LinearLayout) view.findViewById(R.id.container);
            this.imageView = (ImageView) view.findViewById(R.id.item_img);
            this.textContainer = (LinearLayout) view.findViewById(R.id.item_text_container);
            this.content = (TextView) view.findViewById(R.id.item_text);
            this.label = (TextView) view.findViewById(R.id.item_label);
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.info_list_item, viewGroup, false);
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
        customViewHolder.imageView.setImageResource(imgResources[i]);
        customViewHolder.label.setText(mContext.getResources().getString(labelResouces[i]));
        String con="";
        View container = customViewHolder.container;
        Intent intent=null;
        switch(i){
            case 0:
                con = mOffice.getPhone();
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mOffice.getPhone()));
                break;
            case 1:
                con = String.format("%s %s", mOffice.getStreet(), mOffice.getNumber());
                intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(String.format("google.navigation:q=" +
                                                        String.valueOf(mOffice.getLatLng().latitude)
                                                        + ","
                                                        + String.valueOf(mOffice.getLatLng().longitude))));
                break;
            case 2:
                con = mOffice.getWebsite();
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mOffice.getWebsite()));
                break;
            case 3:
                con = "10:00 - 18:00";
                break;
            case 4:
                con = "Łącznie 10 osób";
                break;
        }
        if(intent!=null) {
            container.setOnClickListener(new OnClickIntent(intent));
        }
        final String toCopy = con;
        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (android.content.ClipboardManager)
                                               mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text",
                                                                                            toCopy);
                clipboard.setPrimaryClip(clip);
                Vibrator vi = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vi.vibrate(100);
                Toast.makeText(mContext,
                               mContext.getResources().getString(R.string.toast_copy),
                               Toast.LENGTH_LONG).show();
                return true;
            }
        });
        customViewHolder.content.setText(con);
    }
    @Override
    public int getItemCount() {
        return 5;
    }
}
