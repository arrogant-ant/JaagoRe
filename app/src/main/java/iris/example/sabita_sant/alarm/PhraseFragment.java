package iris.example.sabita_sant.alarm;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import iris.example.sabita_sant.alarm.logic.AlarmMethod;
import iris.example.sabita_sant.alarm.logic.PhraseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhraseFragment extends Fragment implements AlarmMethod{

    View parent;
    private TextView phrase_tv;
    private EditText result_et;
    private PhraseHelper helper;
    private static final String TAG = "PhraseFragment";
    private TextWatcher backspaceWatcher;
    public PhraseFragment() {
        // Required empty public constructor
        backspaceWatcher = new TextWatcher() {
            String oldString;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                Log.i(TAG, "beforeTextChanged: " + charSequence + start + count + after);
                // character has been deleted
                if (after < count) {
                    oldString = String.valueOf(charSequence);
                    return;
                }
                oldString = "";
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.i(TAG, "onTextChanged: " + charSequence + start + before + count);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (oldString.length() != 0)
                    editable.replace(0, editable.length(), oldString);
                Log.i(TAG, "afterTextChanged: " + editable.toString());

            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent =  inflater.inflate(R.layout.fragment_phrase, container, false);
        phrase_tv = parent.findViewById(R.id.phrase_tv);
        result_et = parent.findViewById(R.id.result_et);
        result_et.addTextChangedListener(backspaceWatcher);
        helper = new PhraseHelper();
        populate();
        return parent;
    }

    @Override
    public void populate() {
        phrase_tv.setText(helper.getPhrase());
        result_et.removeTextChangedListener(backspaceWatcher);
        result_et.getText().clear();
        result_et.addTextChangedListener(backspaceWatcher);

    }

    @Override
    public boolean isValidResponse() {
       return helper.isCorrectPhrase(result_et.getText().toString());
    }


}
