package pl.mrokita.mojeokienko.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import pl.mrokita.mojeokienko.R;

public class NewTicketFragment extends Fragment {
    private View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mRootView = inflater.inflate(R.layout.fragment_new_ticket, container, false);
        Spinner officesSpinner = (Spinner) mRootView.findViewById(R.id.offices_spinner);
        officesSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new String[]{"Żoliborz", "Wola"}));
        Spinner windowsSpinner = (Spinner) mRootView.findViewById(R.id.windows_spinner);
        windowsSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new String[]{"A - dowody osobiste", "B - niewiadomo co", "C - też w sumie nie wiem"}));
        windowsSpinner.setEnabled(false);
        windowsSpinner.setClickable(false);
        final EditText ticketInput = (EditText) mRootView.findViewById(R.id.input_number);
        return mRootView;
    }
}
