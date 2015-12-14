package pl.mrokita.mojeokienko;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Api {
    public static final String API_URL = "https://mrokita.pl/v/api/";
    private static String getJsonString(JSONObject obj, String key, String def){
        try {
            return obj.getString(key);
        } catch (JSONException e){
            return def;
        }
    }
    private static Double getJsonDouble(JSONObject obj, String key, Double def){
        try {
            return obj.getDouble(key);
        } catch (JSONException e){
            return def;
        }
    }

    public static String getResponse(String url) throws IOException {
        URL website = new URL(API_URL + url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    public static class Office implements Parcelable {
        private String mId;
        private String mName;
        private String mPhone;
        private String mStreet;
        private Marker mMarker;
        private String mNumber;
        private double lat;
        private double lng;
        private String mDescription;
        private String mImageUrl;

        public Office(JSONObject data){
            mName = getJsonString(data, "name", "");
            mPhone = getJsonString(data, "phone", "");
            mStreet = getJsonString(data, "street", "");
            mNumber = getJsonString(data, "number", "");
            mId = getJsonString(data, "id", "");
            lat = getJsonDouble(data, "lat", 0d);
            lng = getJsonDouble(data, "lng", 0d);
            mDescription = getJsonString(data, "desc", "");
            mImageUrl = getJsonString(data, "img", "");
        }

        public Office(Parcel in){
            mId = in.readString();
            mName = in.readString();
            mPhone = in.readString();
            mStreet = in.readString();
            mNumber = in.readString();
            lat = in.readDouble();
            lng = in.readDouble();
            mDescription = in.readString();
            mImageUrl = in.readString();
        }

        public static final Parcelable.Creator<Office> CREATOR
                = new Parcelable.Creator<Office>() {
            public Office createFromParcel(Parcel in) {
                return new Office(in);
            }

            public Office[] newArray(int size) {
                return new Office[size];
            }
        };

        public String getId(){
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
        public void setMarker(Marker marker){
            mMarker = marker;
        }
        public Marker getMarker(){
            return mMarker;
        }
        public String getImageUrl() {
            return mImageUrl;
        }
        public MarkerOptions getMarkerOptions(){
            return new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .title(mName)
                                    .snippet(String.format("%s %s", mStreet, mNumber));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mId);
            dest.writeString(mName);
            dest.writeString(mPhone);
            dest.writeString(mStreet);
            dest.writeString(mNumber);
            dest.writeDouble(lat);
            dest.writeDouble(lng);
            dest.writeString(mDescription);
            dest.writeString(mImageUrl);
        }
    }

    public interface OnOfficesLoadedListener{
        void onOfficesLoaded(List<Office> offices);
    }

    public static List<Office> getOffices() throws IOException, JSONException {
        List<Office> ret = new ArrayList<>();
        JSONArray res = new JSONArray(getResponse("getOffices"));
        for(int i=0; i<res.length(); i++){
            ret.add(new Office(res.getJSONObject(i)));
        }
        return ret;
    }
}
