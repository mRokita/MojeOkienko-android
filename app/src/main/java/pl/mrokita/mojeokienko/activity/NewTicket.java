package pl.mrokita.mojeokienko.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.fragment.NewTicketFragment;

public class NewTicket extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);
        setupToolbar();
        setupFragment();
    }

    private void setupFragment(){
        Intent intent = getIntent();
        NewTicketFragment newTicketFragment = new NewTicketFragment();
        if(intent != null) {
            if (intent.hasExtra("office"))
                newTicketFragment.setOffice(
                        (Api.Office)intent.getParcelableExtra("office"));
            if (intent.hasExtra("windowQueue"))
                newTicketFragment.setWindowQueue(
                        (Api.WindowQueue) intent.getParcelableExtra("windowQueue"));
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.fl_content, newTicketFragment)
                .commit();
    }

    private void setupToolbar(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.drawer_settings));
        View statusBar = findViewById(R.id.statusbar);
        statusBar.setVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
