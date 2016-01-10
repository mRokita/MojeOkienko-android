package pl.mrokita.mojeokienko.asynctask;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import java.util.List;

import pl.mrokita.mojeokienko.Api;

public class OfficesLoader extends AsyncTask<Void, Void, List<Api.Office>>{
    private Api.OnOfficesLoadedListener listener;
    private View snackbarRoot;
    private SwipeRefreshLayout srl;
    public OfficesLoader(Api.OnOfficesLoadedListener listener, View snackbarRoot){
        this.listener = listener;
        this.snackbarRoot = snackbarRoot;
        this.srl = null;
    }

    public OfficesLoader(Api.OnOfficesLoadedListener listener, View snackbarRoot, SwipeRefreshLayout srl){
        this.listener = listener;
        this.snackbarRoot = snackbarRoot;
        this.srl = srl;
        if(srl!=null) srl.setRefreshing(true);
    }

    @Override
    protected List<Api.Office> doInBackground(Void... params) {
        try {
            return Api.getOffices();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(snackbarRoot, "Nie można było pobrać listy biur", Snackbar.LENGTH_LONG)
                    .setAction("ODŚWIEŻ", new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new OfficesLoader(listener, snackbarRoot, srl).execute();
                        }
                    })
                    .show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Api.Office> offices){
        if(srl!=null) srl.setRefreshing(false);
        if(offices!=null){
            listener.onOfficesLoaded(offices);
        }
    }
}
