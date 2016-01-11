package pl.mrokita.mojeokienko.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.Constants;
import pl.mrokita.mojeokienko.R;
import pl.mrokita.mojeokienko.asynctask.OfficesLoader;
import pl.mrokita.mojeokienko.asynctask.WindowQueuesLoader;
import pl.mrokita.mojeokienko.service.TicketNotificationService;

public class NewTicketFragment extends Fragment implements Api.OnOfficesLoadedListener, Api.OnWindowQueuesLoadedListener{
    private View mRootView;
    private EditText mTicketInput;
    private List<Api.Office> mOffices;
    private Spinner mOfficesSpinner;
    private List<Api.WindowQueue> mWindowQueues;
    private Api.Office mSelectedOffice;
    private boolean mBound = false;
    private Messenger mService;
    private Api.WindowQueue mSelectedWindowQueue;
    private Spinner mWindowsSpinner;
    @Override
    public void onCreate(Bundle savedInstanceState){
        getActivity().bindService(
                new Intent(getActivity().getBaseContext(), TicketNotificationService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mRootView = inflater.inflate(R.layout.fragment_new_ticket, container, false);
        mOfficesSpinner = (Spinner) mRootView.findViewById(R.id.offices_spinner);
        mOfficesSpinner.setEnabled(false);
        mOfficesSpinner.setClickable(false);
        Intent intent = new Intent(getActivity(), TicketNotificationService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mOfficesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWindowsSpinner.setEnabled(false);
                mWindowsSpinner.setClickable(false);
                mTicketInput.setEnabled(false);
                if (mOffices != null) {
                    mSelectedOffice = mOffices.get(position);
                    new WindowQueuesLoader(NewTicketFragment.this, mRootView)
                            .execute(mOffices.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mWindowsSpinner = (Spinner) mRootView.findViewById(R.id.windows_spinner);
        mWindowsSpinner.setEnabled(false);
        mWindowsSpinner.setClickable(false);
        mWindowsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mWindowQueues != null) {
                    mTicketInput.setEnabled(true);
                    mSelectedWindowQueue = mWindowQueues.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mTicketInput = (EditText) mRootView.findViewById(R.id.input_number);
        mTicketInput.setEnabled(false);
        ((Button) mRootView.findViewById(R.id.button_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sendMessage(Constants.ACTION.SET_NOTIFICATION, null);
            }
        });
        new OfficesLoader(this).execute();
        return mRootView;
    }

    @Override
    public void onOfficesLoaded(List<Api.Office> offices) {
        if(offices==null){
            Snackbar.make(mRootView, "Nie można było pobrać listy biur", Snackbar.LENGTH_LONG)
                    .setAction("ODŚWIEŻ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new OfficesLoader(NewTicketFragment.this).execute();
                        }
                    })
                    .show();
            return;
        }
        Activity activity = getActivity();
        if(activity == null) return;
        mOffices = offices;
        String[] labels = new String[mOffices.size()];
        for(int i=0; i<mOffices.size(); i++){
            labels[i] = mOffices.get(i).getName();
        }
        if(mOffices.size()>0){
            mSelectedOffice= mOffices.get(0);
        }
        mOfficesSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, labels));
        mOfficesSpinner.setEnabled(true);
        mOfficesSpinner.setClickable(true);
        if(mWindowQueues == null){
            new WindowQueuesLoader(NewTicketFragment.this, mRootView)
                    .execute(mSelectedOffice.getId());
        }
    }

    @Override
    public void onWindowQueuesLoaded(List<Api.WindowQueue> windowQueues) {
        Activity activity = getActivity();
        if(activity == null) return;
        if(windowQueues.size() == 0) return;
        if(!windowQueues.get(0).getOfficeId().equals(mSelectedOffice.getId())) return;
        mSelectedWindowQueue = windowQueues.get(0);
        mWindowQueues = windowQueues;
        String[] labels = new String[mWindowQueues.size()];
        for(int i=0; i<mWindowQueues.size(); i++){
            labels[i] = mWindowQueues.get(i).getWindowName();
        }

        mTicketInput.setEnabled(true);
        mWindowsSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, labels));
        mWindowsSpinner.setEnabled(true);
        mWindowsSpinner.setClickable(true);

    }
    @Override
    public void onDestroy(){
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }
    private void sendMessage(int action, Bundle data){
        if(mService!=null){
            Message msg = Message.obtain(null, action);
            msg.setData(data);
            try {
                mService.send(msg);
            } catch (RemoteException e) {

            };
        }
    }
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };
}
