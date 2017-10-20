package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Sud on 10/20/17.
 */

public class Qoute implements Serializable{
    String quote;
    String  author;
    boolean shown;

    public String getQuote() {

        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;

    }

    public void setAuthor(String author) {
        this.author = author;

    }
    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

}
