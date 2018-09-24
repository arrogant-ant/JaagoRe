package iris.example.sabita_sant.alarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iris.example.sabita_sant.alarm.logic.AlarmMethod;

public class SimpleFragment extends Fragment implements AlarmMethod {

    View parent;
    public SimpleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_simple, container, false);
        return parent;
    }

    @Override
    public void populate() {

    }

    @Override
    public boolean isValidResponse() {
        return true;
    }
}
