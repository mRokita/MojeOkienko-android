package pl.mrokita.mojeokienko.asynctask;

import android.os.AsyncTask;

import java.util.List;

import pl.mrokita.mojeokienko.Api;

public class OfficesLoader extends AsyncTask<Void, Void, List<Api.Office>>{
    private Api.OnOfficesLoadedListener listener;

    public OfficesLoader(Api.OnOfficesLoadedListener listener){
        this.listener = listener;
    }

    @Override
    protected List<Api.Office> doInBackground(Void... params) {
        try {
            return Api.getOffices();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Api.Office> offices){
        if(listener!=null){
            listener.onOfficesLoaded(offices);
        }
    }
}
