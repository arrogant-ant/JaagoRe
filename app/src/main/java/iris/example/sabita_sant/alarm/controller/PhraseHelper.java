package iris.example.sabita_sant.alarm.controller;

import android.util.Log;

/**
 * Created by Sud on 9/15/18.
 */

public class PhraseHelper {
    private String phrase;
    private static final String TAG = "PhraseHelper";
    public PhraseHelper() {
        phrase = generatePhrase();
    }
    private String generatePhrase() {
        int phraseLen =5;
        char phrase[] = new char[phraseLen];
        // ascii 48 - 122
        for (int i = 0; i <phraseLen ; i++) {
            int r = (int) (48+(Math.random()*100)%75);
            phrase[i] = (char)r;
            Log.i(TAG, "generatePhrase: "+i+" "+phrase[i]+" "+r);
        }
        return new String(phrase);
    }

    public String getPhrase() {
        phrase =generatePhrase();
        return phrase;
    }

    public boolean isCorrectPhrase(String answer){
        Log.i(TAG, "isCorrectPhrase: "+phrase+" ans "+answer);
        return phrase.equals(answer);
    }
}
