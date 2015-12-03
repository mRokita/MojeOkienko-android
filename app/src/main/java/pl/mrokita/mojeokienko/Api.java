package pl.mrokita.mojeokienko;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Api {
    public static class Office implements Serializable{
        private int mId=0;
        private String mName="Urząd Dzielnicy Żoliborz";
        private String mPhone="666 666 666";
        private float lat = 52.2699677f;
        private float lng = 20.9833121f;
        private String mDescription="Lorem impsum dolor\nlel\nnie";
        public Office(){
        }
        public int getId(){
            return mId;
        }
        public String getName(){
            return mName;
        }
        public String getPhone(){
            return mPhone;
        }
        public String getDescription(){
            return mDescription;
        }
        public LatLng getLatLng(){
            return new LatLng(lat, lng);
        }
        public MarkerOptions getMarkerOptions(){
            return new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .title(mName);
        }
    }

    public static List<Office> getOffices() {
        List<Office> ret = new ArrayList<>();
        ret.add(new Office());
        return ret;
    }
}
