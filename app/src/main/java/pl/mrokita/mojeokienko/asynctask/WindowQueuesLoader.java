package pl.mrokita.mojeokienko.asynctask;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import pl.mrokita.mojeokienko.Api;

public class WindowQueuesLoader extends AsyncTask<String, Void, List<Api.WindowQueue>>{
    private Api.OnWindowQueuesLoadedListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public WindowQueuesLoader(Api.OnWindowQueuesLoadedListener listener, SwipeRefreshLayout swipeRefreshLayout){
        this.mListener = listener;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected List<Api.WindowQueue> doInBackground(String... params) {
        try {
            return Api.getWindowQueues(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Api.WindowQueue> windowQueues){
        mListener.onWindowQueuesLoaded(windowQueues);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
