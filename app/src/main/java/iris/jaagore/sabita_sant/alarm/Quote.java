package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Sud on 10/20/17.
 */

public class Quote implements Serializable{
    String quote;
    String  author;

    public Quote() {
        quote="Life is like riding a bicycle. To keep your balance you must keep moving.";
        author="Albert Einstein";
    }

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

}
