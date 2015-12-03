package pl.mrokita.mojeokienko;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

public class OfficeMapFragment extends MapFragment {
    private FloatingActionButton mFab;
    private HashMap<Marker, Api.Office> mMarkerOffices;
    private void addToLayout(RelativeLayout parent, View v, int marginBottom, int marginRight){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.setMargins(0, 0, marginBottom, marginRight);
        parent.addView(v, params);
    }
    public void hideFab(){
        mFab.hide();
    }
    public void showFab(){
        mFab.show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View mapView = super.onCreateView(inflater, viewGroup, bundle);
        RelativeLayout layout = new RelativeLayout(getActivity());
        layout.addView(mapView, new RelativeLayout.LayoutParams(-1, -1));
        View v = inflater.inflate(R.layout.map_info_button, null);
        addToLayout(layout, v, 10, 10);
        mFab = (FloatingActionButton) v;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return layout;
    }

    public void initMarkers(final List<Api.Office> offices){
        mMarkerOffices = new HashMap<>();
        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                for (Api.Office office : offices)
                    mMarkerOffices.put(googleMap.addMarker(office.getMarkerOptions()), office);
            }
        });
    }
    public void setOnMarkerClickListener(final GoogleMap.OnMarkerClickListener listener){
        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMarkerClickListener(listener);
            }
        });
    }
    public void setOnMapClickListener(final GoogleMap.OnMapClickListener listener){
        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(listener);
            }
        });
    }
    public Api.Office getOfficeOfMarker(Marker marker){
        return mMarkerOffices.get(marker);
    }
}
