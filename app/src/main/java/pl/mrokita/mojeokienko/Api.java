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

    private static Integer getJsonInteger(JSONObject obj, String key, Integer def){
        try {
            return obj.getInt(key);
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

    public static class WindowQueue implements Parcelable {
        private int mCurrentNumber;
        private String mOfficeId;
        private String mWindowLetter;
        private String mWindowName;
        private int mClientsInQueue;
        public WindowQueue(JSONObject data){
            mCurrentNumber = getJsonInteger(data, "aktualnyNumer", -1);
            mOfficeId = getJsonString(data, "idUrzedu", null);
            mWindowLetter = getJsonString(data, "literaGrupy", null);
            mWindowName = getJsonString(data, "nazwaGrupy", null);
            mClientsInQueue = getJsonInteger(data, "liczbaKlwKolejce", -1);
        }

        public WindowQueue(Parcel in){
            mCurrentNumber = in.readInt();
            mOfficeId = in.readString();
            mWindowLetter = in.readString();
            mWindowName = in.readString();
            mClientsInQueue = in.readInt();
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mCurrentNumber);
            dest.writeString(mOfficeId);
            dest.writeString(mWindowLetter);
            dest.writeString(mWindowName);
            dest.writeInt(mClientsInQueue);
        }

        public static final Parcelable.Creator<WindowQueue> CREATOR
                = new Parcelable.Creator<WindowQueue>() {
            public WindowQueue createFromParcel(Parcel in) {
                return new WindowQueue(in);
            }

            public WindowQueue[] newArray(int size) {
                return new WindowQueue[size];
            }
        };

        public Integer getCurrentNumber(){
            return mCurrentNumber;
        }
        public String getOfficeId(){
            return mOfficeId;
        }
        public String getWindowLetter(){
            return mWindowLetter;
        }
        public String getWindowName(){
            return mWindowName;
        }
        public Integer getClientsInQueue(){
            return mClientsInQueue;
        }

        @Override
        public int describeContents() {
            return 0;
        }

    }

    public static class TicketInfo implements Parcelable{
        private int mCurrentNumber;
        private int mAvgTime;
        private String mOfficeId;
        private int mClientCount;
        private String mWindowLetter;
        private String mWindowName;
        private int mNumber;
        private int mClientsBefore;
        private int mTimeSpentOnCurrent;
        private int mExpectedTimeLeft;

        public TicketInfo(JSONObject data){
            mCurrentNumber = getJsonInteger(data, "aktualnyNumer", -1);
            mAvgTime = getJsonInteger(data, "czasObslugi", -1);
            if(mAvgTime == 0) mAvgTime = 3;
            mOfficeId = getJsonString(data, "idUrzedu", null);
            mClientCount = getJsonInteger(data, "liczbaKlwKolejce", -1);
            mWindowLetter = getJsonString(data, "literaGrupy", null);
            mWindowName = getJsonString(data, "nazwaGrupy", null);
            mNumber = getJsonInteger(data, "numerek", -1);
            mClientsBefore = getJsonInteger(data, "numeryPrzed", -1);
            mTimeSpentOnCurrent = getJsonInteger(data, "timeSpentOnCurrent", -1);
            mExpectedTimeLeft = getJsonInteger(data, "zostaloCzasu", -1);
        }

        public TicketInfo(TicketInfo other){
            mCurrentNumber = other.getCurrentNumber();
            mAvgTime = other.getAvgTime();
            mOfficeId = other.getOfficeId();
            mClientCount = other.getClientCount();
            mWindowLetter = other.getWindowLetter();
            mWindowName = other.getWindowName();
            mNumber = other.getNumber();
            mClientsBefore = other.getClientsBefore();
            mTimeSpentOnCurrent = other.getTimeSpentOnCurrent();
            mExpectedTimeLeft = other.getExpectedTimeLeft();
        }

        public boolean equals(TicketInfo other){
            return (other!=null &&
                    mOfficeId.equals(other.getOfficeId()) &&
                    mWindowLetter.equals(other.getWindowLetter()) &&
                    mNumber == other.getNumber());
        }
        public TicketInfo(Parcel in){
            mCurrentNumber = in.readInt();
            mAvgTime = in.readInt();
            mOfficeId = in.readString();
            mClientCount = in.readInt();
            mWindowLetter = in.readString();
            mWindowName = in.readString();
            mNumber = in.readInt();
            mClientsBefore = in.readInt();
            mTimeSpentOnCurrent = in.readInt();
            mExpectedTimeLeft = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mCurrentNumber);
            dest.writeInt(mAvgTime);
            dest.writeString(mOfficeId);
            dest.writeInt(mClientCount);
            dest.writeString(mWindowLetter);
            dest.writeString(mWindowName);
            dest.writeInt(mNumber);
            dest.writeInt(mClientsBefore);
            dest.writeInt(mTimeSpentOnCurrent);
            dest.writeInt(mExpectedTimeLeft);
        }

        public static final Parcelable.Creator<Api.TicketInfo> CREATOR
                = new Parcelable.Creator<Api.TicketInfo>() {
            public TicketInfo createFromParcel(Parcel in) {
                return new TicketInfo(in);
            }

            public TicketInfo[] newArray(int size) {
                return new TicketInfo[size];
            }
        };
        public int getCurrentNumber(){
            return mCurrentNumber;
        }
        public int getAvgTime(){
            return mAvgTime;
        }
        public String getOfficeId(){
            return mOfficeId;
        }
        public int getClientCount(){
            return mClientCount;
        }
        public String getWindowLetter(){
            return mWindowLetter;
        }
        public String getWindowName(){
            return mWindowName;
        }
        public int getNumber(){
            return mNumber;
        }
        public int getClientsBefore(){
            return mClientsBefore;
        }
        public int getTimeSpentOnCurrent(){
            return mTimeSpentOnCurrent;
        }
        public int getExpectedTimeLeft(){
            return mExpectedTimeLeft;
        }

        @Override
        public int describeContents() {
            return 0;
        }
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
        private String mWebsite;
        private int mClientCount;
        private String mOpenTime;
        public Office(JSONObject data){
            mName = getJsonString(data, "name", null);
            mPhone = getJsonString(data, "phone", null);
            mStreet = getJsonString(data, "street", null);
            mNumber = getJsonString(data, "number", null);
            mId = getJsonString(data, "id", null);
            lat = getJsonDouble(data, "lat", 0d);
            lng = getJsonDouble(data, "lng", 0d);
            mDescription = getJsonString(data, "desc", null);
            mImageUrl = getJsonString(data, "img", null);
            mWebsite = getJsonString(data, "www", null);
            mClientCount = getJsonInteger(data, "clientCount", 0);
            mOpenTime = getJsonString(data, "openTime", "10:00 - 16:00");
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
            mWebsite = in.readString();
            mClientCount = in.readInt();
            mOpenTime = in.readString();
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
        public String getStreet() {
            return mStreet;
        }
        public String getNumber(){
            return mNumber;
        }
        public String getPhone(){
            return mPhone;
        }
        public int getClientCount(){ return mClientCount; }
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
        public String getWebsite(){
            return mWebsite;
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
            dest.writeString(mWebsite);
            dest.writeInt(mClientCount);
            dest.writeString(mOpenTime);
        }
    }

    public interface OnWindowQueuesLoadedListener{
        void onWindowQueuesLoaded(List<WindowQueue> windowQueues);
    }

    public interface OnOfficesLoadedListener{
        void onOfficesLoaded(List<Office> offices);
    }

    public static TicketInfo getTicketInfo(String officeId, String windowLetter, String ticketId) throws IOException, JSONException{
        return new TicketInfo(new JSONObject(getResponse("getTicketInfo/"+officeId+"/"+windowLetter+"/"+ticketId)));
    }

    public static List<WindowQueue> getWindowQueues(String officeId) throws IOException, JSONException {
        List<WindowQueue> ret = new ArrayList<>();
        JSONArray res = new JSONArray(getResponse("getQueues/"+officeId));
        for(int i=0; i<res.length(); i++){
            ret.add(new WindowQueue(res.getJSONObject(i)));
        }
        return ret;
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
