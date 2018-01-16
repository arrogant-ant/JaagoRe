package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Sud on 10/20/17.
 */

public class Quote implements Serializable{
    int s_no;
    String quote;
    String  author;

    public Quote() {
        s_no=0;
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

    public int getS_no() {
        return s_no;
    }

    public void setS_no(int s_no) {
        this.s_no = s_no;
    }
}
