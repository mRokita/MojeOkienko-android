package pl.mrokita.mojeokienko.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import pl.mrokita.mojeokienko.activity.OfficeInfo;
import pl.mrokita.mojeokienko.asynctask.OfficesLoader;

public class OfficeMapFragment extends Fragment implements Api.OnOfficesLoadedListener{
    private FloatingActionButton mFab;
    private RelativeLayout mLayout;
    private MapView mMap;
    private Context mContext;
    private String mCurrentMarkerId;
    private HashMap<String, Api.Office> mIdToOffice;
    private HashMap<Marker, Api.Office> mMarkerToOffice;
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

    public void setData(MapView map, Context context, String lastMarkerId){
        this.mMap = map;
        this.mContext = context;
        this.mCurrentMarkerId = lastMarkerId;
    }
    /* Inicjalizacja mapy */
    public void createMap(){
        /* Tworzenie głównego layoutu */
        mLayout = new RelativeLayout(mContext);
        if (((View)mMap).getParent()==null){
            mLayout.addView(mMap, new RelativeLayout.LayoutParams(-1, -1));
        }
        /* Floating Action Button */
        mFab = (FloatingActionButton) LayoutInflater.from(mContext)
                .inflate(R.layout.map_info_button, null);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficeMapFragment.this.getActivity(), OfficeInfo.class);
                intent.putExtra("office", mIdToOffice.get(mCurrentMarkerId));
                startActivity(intent);
            }
        });
        addFabToLayout(mFab, 10, 10);
        mFab.hide();
        /* Konfiguracja mapy */
        mMap.onCreate(new Bundle());
        configureMap();
        setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                OfficeMapFragment.this.setCurrentMarkerId(mMarkerToOffice.get(marker).getId());
                OfficeMapFragment.this.showFab();
                return false;
            }
        });
        setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                OfficeMapFragment.this.setCurrentMarkerId(null);
                OfficeMapFragment.this.hideFab();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        if(mMap == null)
            ((MojeOkienko) getActivity()).putMapToOfficeMapFragment(this, mCurrentMarkerId);
        mMap.onResume();
        if (mMarkerToOffice == null)
            new OfficesLoader(OfficeMapFragment.this).execute();
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
        mMarkerToOffice = new HashMap<>();
        mIdToOffice = new HashMap<>();
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for (Api.Office office : offices) {
                    Marker m = googleMap.addMarker(office.getMarkerOptions());
                    office.setMarker(m);
                    mMarkerToOffice.put(m, office);
                    mIdToOffice.put(office.getId(), office);
                }
                selectMarker(mCurrentMarkerId);
            }
        });
    }

    public void setOnMarkerClickListener(final GoogleMap.OnMarkerClickListener listener) {
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMarkerClickListener(listener);
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
        return mMarkerToOffice.get(marker);
    }

    public Api.Office getOffice(String officeId){
        return mIdToOffice.get(officeId);
    }

    public void setCurrentMarkerId(String officeId){
        mCurrentMarkerId = officeId;
    }

    public String getCurrentMarkerId(){
        return mCurrentMarkerId;
    }

    public void selectMarker(String officeId){
        if(officeId!=null) {
            Marker m = (mIdToOffice.get(officeId).getMarker());
            m.showInfoWindow();
            showFab();
        }
    }

    @Override
    public void onOfficesLoaded(List<Api.Office> offices) {
        if(offices!=null)
            initMarkers(offices);
        else {
            Snackbar.make(mMap, "Nie można było pobrać listy biur", Snackbar.LENGTH_LONG)
                    .setAction("ODŚWIEŻ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new OfficesLoader(OfficeMapFragment.this).execute();
                        }
                    })
                    .show();
        }
    }
}
