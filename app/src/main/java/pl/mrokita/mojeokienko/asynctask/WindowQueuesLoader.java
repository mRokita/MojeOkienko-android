package pl.mrokita.mojeokienko.asynctask;

import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;

import java.util.List;

import pl.mrokita.mojeokienko.Api;

public class WindowQueuesLoader extends AsyncTask<String, Void, List<Api.WindowQueue>>{
    private Api.OnWindowQueuesLoadedListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View snackbarRoot;

    public WindowQueuesLoader(Api.OnWindowQueuesLoadedListener listener, View snackbarRoot){
        this.listener = listener;
        this.snackbarRoot = snackbarRoot;
        this.swipeRefreshLayout = null;
    }

    public WindowQueuesLoader(Api.OnWindowQueuesLoadedListener listener, View snackbarRoot, SwipeRefreshLayout swipeRefreshLayout){
        this.listener = listener;
        this.swipeRefreshLayout = swipeRefreshLayout;
        if(swipeRefreshLayout!=null)
            this.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    WindowQueuesLoader.this.swipeRefreshLayout.setRefreshing(true);
                }
            });
        this.snackbarRoot = snackbarRoot;
    }

    @Override
    protected List<Api.WindowQueue> doInBackground(String... params) {
        try {
            return Api.getWindowQueues(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(snackbarRoot, "Nie można było pobrać informacji o kolejkach", Snackbar.LENGTH_LONG)
                    .setAction("ODŚWIEŻ", new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new WindowQueuesLoader(listener, snackbarRoot, swipeRefreshLayout).execute();
                        }
                    })
                    .show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Api.WindowQueue> windowQueues){
        if(windowQueues != null && listener!=null)
            listener.onWindowQueuesLoaded(windowQueues);
        if(swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
    }
}
