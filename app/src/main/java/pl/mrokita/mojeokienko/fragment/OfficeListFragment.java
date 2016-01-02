package pl.mrokita.mojeokienko.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.adapter.OfficeListRVAdapter;
import pl.mrokita.mojeokienko.asynctask.OfficesLoader;

public class OfficeListFragment extends Fragment implements Api.OnOfficesLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private View mRootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private OfficeListRVAdapter mOfficeListRVAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_office_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mContext = getActivity();
        setupRV();
        new OfficesLoader(this).execute();
        return mRootView;
    }

    @Override
    public void onOfficesLoaded(List<Api.Office> offices) {
        mOfficeListRVAdapter.setOffices(offices);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public class RecyclerViewSetUp implements Runnable {
        @Override
        public void run() {
            mRecyclerView.setAdapter(mOfficeListRVAdapter);
        }
    }
    private void setupRV(){
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mOfficeListRVAdapter = new OfficeListRVAdapter(mContext);
        mOfficeListRVAdapter.setOffices(new ArrayList<Api.Office>());
        new Thread(new RecyclerViewSetUp()).start();
    }

    @Override
    public void onRefresh() {
        new OfficesLoader(this).execute();
    }
}
