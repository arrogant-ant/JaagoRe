package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Sud on 10/20/17.
 */
@Entity(tableName = "Quotes")
public class Quote implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "s_no")
    int s_no;

    @ColumnInfo(name = "quote")
    String quote;

    @ColumnInfo(name = "author")
    String author;

    public Quote() {
        s_no = -1;
        quote = "Life is like riding a bicycle. To keep your balance you must keep moving.";
        author = "Albert Einstein";
    }

    public Quote(int s_no, String quote, String author) {
        this.s_no = s_no;
        this.quote = quote;
        this.author = author;
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
