package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Sud on 10/20/17.
 */

class QuoteHelper {

    Context context;
    final String FILENAME="quote.txt";

    public QuoteHelper(Context context) {
        this.context = context;
    }

    public ArrayList<Quote> readQuote()
    {
        ArrayList<Quote> quotes=new ArrayList<>();
        Quote quote=new Quote();
        try {
            FileInputStream fis=context.openFileInput(FILENAME);
            ObjectInputStream ois=new ObjectInputStream(fis);
            while((quote = (Quote) ois.readObject())!=null)
            {
                quotes.add(quote);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(quotes.size()==0)
        {
            quotes.add(quote);
        }
        return quotes;

    }
    public void writeQuote(ArrayList<Quote> quotes)
    {

        try {
            FileOutputStream fos =context.openFileOutput(FILENAME,Context.MODE_PRIVATE);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            for(Quote quote:quotes)
                oos.writeObject(quote);

            oos.flush();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
