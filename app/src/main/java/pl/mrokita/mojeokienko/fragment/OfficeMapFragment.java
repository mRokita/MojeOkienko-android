package pl.mrokita.mojeokienko.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.activity.MojeOkienko;
import pl.mrokita.mojeokienko.asynctask.OfficesLoader;

public class OfficeMapFragment extends Fragment implements Api.OnOfficesLoadedListener{
    private FloatingActionButton mFab;
    private RelativeLayout mLayout;
    private MapView mMap;
    private int mCount = 0;
    private Context mContext;
    private HashMap<Marker, Api.Office> mMarkerOffices;
    /* Dodawanie przycisku informacji */
    private void addFabToLayout(View v, int marginBottom, int marginRight){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.setMargins(0, 0, marginRight, marginBottom);
        mLayout.addView(v, params);
    }

    public void hideFab(){
        mFab.hide();
    }

    public void showFab(){
        mFab.show();
    }

    public void setMap(MapView map, Context context){
        this.mMap = map;
        this.mContext = context;
    }
    /* Inicjalizacja mapy */
    public void createMap(){
        /* Tworzenie głównego layoutu */
        mCount++;
        mLayout = new RelativeLayout(mContext);
        if (((View)mMap).getParent()==null){
            mLayout.addView(mMap, new RelativeLayout.LayoutParams(-1, -1));
        }
        /* Floating Action Button */
        mFab = (FloatingActionButton) LayoutInflater.from(mContext)
                .inflate(R.layout.map_info_button, null);
        addFabToLayout(mFab, 10, 10);
        mFab.hide();
        /* Konfiguracja mapy */
        mMap.onCreate(new Bundle());
        configureMap();
        setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("MARKER", OfficeMapFragment.this.getOfficeOfMarker(marker).getName());
                OfficeMapFragment.this.showFab();
                return false;
            }
        });
        setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                OfficeMapFragment.this.hideFab();
            }
        });
        new OfficesLoader(OfficeMapFragment.this).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        if(mMap == null)
            ((MojeOkienko) getActivity()).putMapToOfficeMapFragment(this);
        mMap.onResume();
        return mLayout;
    }

    public void configureMap(){
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(52.25, 21.0)));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
        });
    }
    /* Inicjalizacja markerów */
    public void initMarkers(final List<Api.Office> offices){
        mMarkerOffices = new HashMap<>();
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for (Api.Office office : offices)
                    mMarkerOffices.put(googleMap.addMarker(office.getMarkerOptions()), office);
            }
        });
    }

    public void setOnMarkerClickListener(final GoogleMap.OnMarkerClickListener listener) {
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMarkerClickListener(listener);
                Log.e("set", "set");
            }
        });
    }

    public void setOnMapClickListener(final GoogleMap.OnMapClickListener listener) {
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(listener);
            }
        });
    }

    public Api.Office getOfficeOfMarker(Marker marker){
        return mMarkerOffices.get(marker);
    }

    @Override
    public void onOfficesLoaded(List<Api.Office> offices) {
        initMarkers(offices);
    }
}
