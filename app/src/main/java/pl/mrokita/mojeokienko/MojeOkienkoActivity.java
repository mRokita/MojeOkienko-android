package pl.mrokita.mojeokienko;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MojeOkienkoActivity extends AppCompatActivity {
    private GoogleMap map;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private OfficeMapFragment mOfficeMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOfficeMapFragment = new OfficeMapFragment();
        setContentView(R.layout.activity_moje_okienko);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            setupDrawer();
        }
        View statusBar = findViewById(R.id.statusbar);
        statusBar.setVisibility(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ?
                View.VISIBLE : View.GONE);
        setupFragment();
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupNavigationView();
    }

    private void setupNavigationView(){
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.drawer_ticket:
                        fragment = new NewTicketFragment();
                        addFragment(fragment);
                        return true;
                    case R.id.drawer_map:
                        if(mOfficeMapFragment==null){
                            mOfficeMapFragment = new OfficeMapFragment();
                            mOfficeMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    Log.e("MARKER", mOfficeMapFragment.getOfficeOfMarker(marker).getName());
                                    mOfficeMapFragment.showFab();
                                    return false;
                                }
                            });
                            mOfficeMapFragment.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    mOfficeMapFragment.hideFab();
                                }
                            });
                        }
                        mOfficeMapFragment.initMarkers(Api.getOffices());
                        fragment = mOfficeMapFragment;
                        addFragment(fragment);
                        return true;
                    case R.id.drawer_settings:
                        Intent intent = new Intent(MojeOkienkoActivity.this, PrefsActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
    private void setupFragment(){
        Fragment fragment = new NewTicketFragment();
        addFragment(fragment);
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
    }

    public void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.fl_content, fragment)
                .commit();
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
