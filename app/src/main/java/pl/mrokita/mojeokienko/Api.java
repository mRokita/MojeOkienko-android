package pl.mrokita.mojeokienko;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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

    public static class Office implements Serializable{
        private String mId;
        private String mName;
        private String mPhone;
        private String mStreet;
        private String mNumber;
        private double lat;
        private double lng;
        private String mDescription;

        public Office(JSONObject data){
            mName = getJsonString(data, "name", "");
            mPhone = getJsonString(data, "phone", "");
            mStreet = getJsonString(data, "street", "");
            mNumber = getJsonString(data, "number", "");
            mId = getJsonString(data, "id", "");
            lat = getJsonDouble(data, "lat", 0d);
            lng = getJsonDouble(data, "lng", 0d);
            mDescription = getJsonString(data, "desc", "");
        }
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
        public MarkerOptions getMarkerOptions(){
            return new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .title(mName)
                                    .snippet(String.format("%s %s", mStreet, mNumber));
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
