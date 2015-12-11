package pl.mrokita.mojeokienko.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import pl.mrokita.mojeokienko.R;

public class NewTicketFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.e("CREATE", "LEL");
        return inflater.inflate(R.layout.fragment_new_ticket, container, false);
    }
}
