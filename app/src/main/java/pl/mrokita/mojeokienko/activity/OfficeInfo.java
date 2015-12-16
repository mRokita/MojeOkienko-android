package pl.mrokita.mojeokienko.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;

public class OfficeInfo extends AppCompatActivity {
    Api.Office mOffice;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_info);
        Intent i = getIntent();
        mOffice = i.getExtras().getParcelable("office");
        setupToolbar();
        getSupportActionBar().setTitle(mOffice.getName());
        ((TextView)findViewById(R.id.telefon)).setText(mOffice.getPhone());
        ((TextView)findViewById(R.id.adres)).setText(String.format("%s %s", mOffice.getStreet(), mOffice.getNumber()));
        ((TextView)findViewById(R.id.www)).setText(mOffice.getWebsite());
        new DownloadImageTask((ImageView) findViewById(R.id.img_header))
                .execute(mOffice.getImageUrl());
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //View statusBar = findViewById(R.id.statusbar);
        //statusBar.setVisibility(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ?
        //        View.VISIBLE : View.GONE);
    }

    public void doCall(View v){
        Log.e("CALL", "CALL");
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
