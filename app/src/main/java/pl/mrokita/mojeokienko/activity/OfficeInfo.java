package pl.mrokita.mojeokienko.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.InputStream;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.Constants;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.adapter.OfficeInfoRVAdapter;

public class OfficeInfo extends AppCompatActivity {
    SharedPreferences mPreferences;
    Api.Office mOffice;
    FloatingActionButton mFab;
    AppBarLayout mAppBarLayout;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_info);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent i = getIntent();
        mOffice = i.getExtras().getParcelable("office");
        setupToolbar();
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                                                    findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mOffice.getName());
//        mFab = (FloatingActionButton) findViewById(R.id.fab);
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(OfficeInfo.this, NewTicket.class);
//                intent.putExtra("office", mOffice);
//                startActivity(intent);
//            }
//        });
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new OfficeInfoRVAdapter(this, mOffice));
        if(mPreferences.getBoolean(Constants.PREFERENCE.LOAD_OFFICE_PHOTOS, true))
            new DownloadImageTask((ImageView) findViewById(R.id.img_header))
                .execute(mOffice.getImageUrl());
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mOffice.getName());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap officePhoto;
            try {
                InputStream in = new java.net.URL(url).openStream();
                officePhoto = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                return null;
            }
            return officePhoto;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                bmImage.setImageBitmap(result);
                mAppBarLayout.setExpanded(true, true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
