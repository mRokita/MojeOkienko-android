package pl.mrokita.mojeokienko.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

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
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.drawer_settings));
        View statusBar = findViewById(R.id.statusbar);
        statusBar.setVisibility(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ?
                View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
