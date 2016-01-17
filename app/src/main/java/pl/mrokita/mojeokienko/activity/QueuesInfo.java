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
    private Api.Office mOffice;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queues_info);
        Intent i = getIntent();
        mOffice = i.getParcelableExtra("office");
        setupSwipeRefreshLayout();
        new WindowQueuesLoader(this, mSwipeRefreshLayout).execute(mOffice.getId());
        setupRV();
        setupToolbar();
    }
    private void setupSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new WindowQueuesLoader(QueuesInfo.this, mSwipeRefreshLayout, mSwipeRefreshLayout)
                        .execute(mOffice.getId());
            }
        });
    }
    private void setupRV(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQueuesInfoAdapter = new QueuesInfoRVAdapter(this, mOffice);
        mRecyclerView.setAdapter(mQueuesInfoAdapter);
    }
    private void setupToolbar(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            findViewById(R.id.coordinator_layout).setFitsSystemWindows(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.drawer_settings));
        View statusBar = findViewById(R.id.statusbar);
        statusBar.setVisibility(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ?
                View.VISIBLE : View.GONE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onWindowQueuesLoaded(List<Api.WindowQueue> windowQueues) {
        mQueuesInfoAdapter.setWindowQueues(windowQueues);
    }
}
