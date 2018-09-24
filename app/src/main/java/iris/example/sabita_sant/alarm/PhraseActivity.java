package iris.example.sabita_sant.alarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import iris.example.sabita_sant.alarm.logic.PhraseHelper;

public class PhraseActivity extends AppCompatActivity {

    private TextView phrase_tv;
    private EditText phrase_et;
    private PhraseHelper helper;
    private static final String TAG = "PhraseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase);
        helper = new PhraseHelper();
        phrase_tv = findViewById(R.id.phrase_tv);
        phrase_et = findViewById(R.id.phrase_et);
        phrase_et.addTextChangedListener(new TextWatcher() {
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
        });
        phrase_tv.setText(helper.getPhrase());
    }

    public void check(View view) {
        String phrase = phrase_et.getText().toString();
        if (phrase.equals("")) {
            phrase_et.setHint("EMPTY");
            return;
        }
        if (helper.isCorrectPhrase(phrase))
            phrase_et.setHint("CORRECT");
        else {
            phrase_et.setHint("RETRY");
            phrase_tv.setText(helper.getPhrase());
        }
    }

}
