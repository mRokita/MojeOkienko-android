package pl.mrokita.mojeokienko.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.google.android.material.snackbar.Snackbar;
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
    private Messenger mService;
    private Api.WindowQueue mSelectedWindowQueue;
    private Spinner mWindowsSpinner;

    public void setOffice(Api.Office office){
        mSelectedOffice = office;
    }

    public void setWindowQueue(Api.WindowQueue windowQueue){
        mSelectedWindowQueue = windowQueue;
    }

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
        mWindowsSpinner = (Spinner) mRootView.findViewById(R.id.windows_spinner);
        mWindowsSpinner = (Spinner) mRootView.findViewById(R.id.windows_spinner);
        mTicketInput = (EditText) mRootView.findViewById(R.id.input_number);
        if(mSelectedOffice == null)
            setupOfficesSpiner();
        else {
            mOfficesSpinner.setVisibility(View.GONE);
            mRootView.findViewById(R.id.spacer_offices).setVisibility(View.GONE);
        }
        if(mSelectedWindowQueue == null) {
            setupWindowsSpinner();
            if(mSelectedOffice != null)
                new WindowQueuesLoader(NewTicketFragment.this, mRootView)
                        .execute(mSelectedOffice.getId());
        }
        else {
            mWindowsSpinner.setVisibility(View.GONE);
            mRootView.findViewById(R.id.spacer_window_queues).setVisibility(View.GONE);
        }

        ((Button) mRootView.findViewById(R.id.button_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: zrobić to ładniej, np. jakimś asynctaskiem lub lepiej zaimplementować wątek
                new Thread() {
                    private ProgressDialog progressDialog = null;
                    private boolean canceled;

                    public void run() {
                        NewTicketFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog = new ProgressDialog(NewTicketFragment.this.getActivity());
                                progressDialog.setTitle(R.string.loading_title);
                                progressDialog.setMessage("Pobieranie informacji o numerku...");
                                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        canceled = true;
                                    }
                                });
                                progressDialog.show();
                            }
                        });
                        try {
                            Api.TicketInfo ticketInfo = Api.getTicketInfo(mSelectedOffice.getId(),
                                    mSelectedWindowQueue.getWindowLetter(),
                                    mTicketInput.getText().toString());
                            if (!canceled) {
                                if (ticketInfo.getWindowName() == null) {
                                    Snackbar.make(NewTicketFragment.this.mRootView,
                                            "Podano nieprawidłowe dane", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Bundle b = new Bundle();
                                    b.putParcelable("ticketInfo", ticketInfo);
                                    sendMessage(Constants.ACTION.SET_NOTIFICATION, b);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(NewTicketFragment.this.mRootView,
                                    "Sprawdź połączenie z internetem", Snackbar.LENGTH_SHORT).show();
                        }
                        NewTicketFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                            }
                        });
                    }
                }.start();
            }
        });
        return mRootView;
    }

    public void setupOfficesSpiner(){
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
        new OfficesLoader(this).execute();
    }

    public void setupWindowsSpinner(){
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
        mTicketInput.setEnabled(false);
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
            Api.WindowQueue q = mWindowQueues.get(i);
            labels[i] = q.getWindowLetter() + " - " + q.getWindowName();
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
                e.printStackTrace();
            };
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    static class IncomingHandler extends Handler {
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
