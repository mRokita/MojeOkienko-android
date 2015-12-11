package pl.mrokita.mojeokienko.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.MapView;

import pl.mrokita.mojeokienko.fragment.NewTicketFragment;
import pl.mrokita.mojeokienko.fragment.OfficeMapFragment;
import pl.mrokita.mojeokienko.R;

public class MojeOkienko extends AppCompatActivity {
    private final String STATE_FRAGMENT = "lastFragment";
    private MapView mMap;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private OfficeMapFragment mOfficeMapFragment;
    private int mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moje_okienko);
        setupToolbar();
        setupMap();
        setupFragment();
        setupNavigationView();
        mCurrentFragment = R.id.drawer_ticket;
        if(savedInstanceState!=null)
            mCurrentFragment = savedInstanceState.getInt(STATE_FRAGMENT);
        selectMenuItem(mCurrentFragment);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(STATE_FRAGMENT, mCurrentFragment);
        super.onSaveInstanceState(savedInstanceState);
    }
    private void setupToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        View statusBar = findViewById(R.id.statusbar);
        statusBar.setVisibility(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ?
                View.VISIBLE : View.GONE);
    }
    
    private void setupMap(){
        mMap = new MapView(this);
        mOfficeMapFragment = new OfficeMapFragment();
        putMapToOfficeMapFragment(mOfficeMapFragment);
        mOfficeMapFragment.createMap();
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            setupDrawer();
        }
    }

    public void putMapToOfficeMapFragment(OfficeMapFragment fragment){
        fragment.setMap(mMap, this);
    }

    public MapView getMap(){
        return mMap;
    }

    private void setupNavigationView(){
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return selectMenuItem(menuItem.getItemId());
            }
        });
    }

    private boolean selectMenuItem(int id){
        Fragment fragment;
        switch (id) {
            case R.id.drawer_ticket:
                fragment = new NewTicketFragment();
                addFragment(fragment, id);
                return true;
            case R.id.drawer_map:
                if(mOfficeMapFragment == null || mMap == null) {
                    setupMap();
                }
                fragment = mOfficeMapFragment;
                addFragment(fragment, id);
                return true;
            case R.id.drawer_settings:
                Intent intent = new Intent(MojeOkienko.this, Preferences.class);
                startActivity(intent);
                return true;
        }

        return false;
    }

    private void setupFragment(){
        Fragment fragment = new NewTicketFragment();
        addFragment(fragment, R.id.drawer_ticket);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerLayout.closeDrawers();
    }

    public void addFragment(Fragment fragment, int id){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.fl_content, fragment)
                       .commit();

        mCurrentFragment = id;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_moje_okienko, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
