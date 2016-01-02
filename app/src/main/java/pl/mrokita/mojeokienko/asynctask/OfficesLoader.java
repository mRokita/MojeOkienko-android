package pl.mrokita.mojeokienko.asynctask;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import pl.mrokita.mojeokienko.Api;

public class OfficesLoader extends AsyncTask<Void, Void, List<Api.Office>>{
    private Api.OnOfficesLoadedListener mListener;
    public OfficesLoader(Api.OnOfficesLoadedListener listener){
        this.mListener = listener;
    }

    @Override
    protected List<Api.Office> doInBackground(Void... params) {
        try {
            return Api.getOffices();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Api.Office>();
        }
    }

    @Override
    protected void onPostExecute(List<Api.Office> offices){
        mListener.onOfficesLoaded(offices);
    }
}
