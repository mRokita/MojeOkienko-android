package pl.mrokita.mojeokienko.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.adapter.QueuesInfoRVAdapter;
import pl.mrokita.mojeokienko.asynctask.WindowQueuesLoader;

public class QueuesInfo extends AppCompatActivity implements Api.OnWindowQueuesLoadedListener{
    private RecyclerView mRecyclerView;
    private QueuesInfoRVAdapter mQueuesInfoAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mOfficeId;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queues_info);
        Intent i = getIntent();
        mOfficeId = i.getStringExtra("officeId");
        setupSwipeRefreshLayout();
        new WindowQueuesLoader(this, mSwipeRefreshLayout).execute(mOfficeId);
        setupRV();
        setupToolbar();
    }
    private void setupSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new WindowQueuesLoader(QueuesInfo.this, mSwipeRefreshLayout).execute(mOfficeId);
            }
        });
    }
    private void setupRV(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQueuesInfoAdapter = new QueuesInfoRVAdapter(this);
        mRecyclerView.setAdapter(mQueuesInfoAdapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                Log.e("lel", String.valueOf(NavUtils.shouldUpRecreateTask(this, upIntent)));
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onWindowQueuesLoaded(List<Api.WindowQueue> windowQueues) {
        mQueuesInfoAdapter.setWindowQueues(windowQueues);
    }
}