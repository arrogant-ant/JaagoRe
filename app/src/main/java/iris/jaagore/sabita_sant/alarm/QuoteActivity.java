package iris.jaagore.sabita_sant.alarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class QuoteActivity extends AppCompatActivity {
    TextView title,quote,author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        title= (TextView) findViewById(R.id.quote_title);
        quote= (TextView) findViewById(R.id.quote);
        author= (TextView) findViewById(R.id.aurthor);
        setUI();

    }

    private void setUI() {
        Qoute quote;
        QuoteHelper helper=new QuoteHelper();


    }

    public void done(View view) {
        finish();
    }
}
