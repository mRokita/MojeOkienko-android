package pl.mrokita.mojeokienko.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private View mRootView = null;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private OfficeListRVAdapter mOfficeListRVAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mDoneRefreshing=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_office_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
        mContext = getActivity();
        setupRV();
        doRefresh();
        return mRootView;
    }
    public void doRefresh(){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(!mDoneRefreshing)
                    mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        new OfficesLoader(OfficeListFragment.this).execute();
    }
    @Override
    public void onOfficesLoaded(List<Api.Office> offices) {
        if(offices==null){
            Snackbar.make(mRootView, "Nie można było pobrać listy biur", Snackbar.LENGTH_LONG)
                    .setAction("ODŚWIEŻ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new OfficesLoader(OfficeListFragment.this).execute();
                        }
                    })
                    .show();
        } else if (getActivity() != null)
            mOfficeListRVAdapter.setOffices(offices);
        mSwipeRefreshLayout.setRefreshing(false);
        mDoneRefreshing = true;
    }

    private void setupRV(){
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mOfficeListRVAdapter = new OfficeListRVAdapter(mContext);
        mOfficeListRVAdapter.setOffices(new ArrayList<Api.Office>());
        mRecyclerView.setAdapter(mOfficeListRVAdapter);
    }

    @Override
    public void onRefresh() {
        new OfficesLoader(this).execute();
    }
}
