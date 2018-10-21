package iris.example.sabita_sant.alarm.views;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Stack;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.backend.Quote;
import iris.example.sabita_sant.alarm.controller.QuoteHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuoteFragment extends Fragment {

    private ImageView next, prev;
    private QuoteHelper helper;
    private TextView quote_tv, author_tv;
    private Stack<Quote> recentQuotes;
    private static final String TAG = "QuoteFragment";
    private Quote currentQuote; // presently displayed

    public QuoteFragment() {
        // Required empty public constructor
        recentQuotes = new Stack<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_quote, container, false);
        helper = new QuoteHelper(getContext());
        quote_tv = parent.findViewById(R.id.quote);
        author_tv = parent.findViewById(R.id.author);
        Quote quote = helper.getQuote();
        updateUI(quote);
        next = parent.findViewById(R.id.next_quote);
        prev = parent.findViewById(R.id.prev_quote);
        prev.setVisibility(View.GONE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recentQuotes.push(currentQuote); // putting the currentQuote in stack
                Quote quote = helper.getQuote();
                updateUI(quote);
                prev.setVisibility(View.VISIBLE);
                Log.i(TAG, "onClick: next " + recentQuotes);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quote lastQuote = recentQuotes.pop();
                if (recentQuotes.empty())
                    prev.setVisibility(View.GONE);
                updateUI(lastQuote);
                Log.i(TAG, "onClick: prev " + recentQuotes);
            }
        });

        return parent;
    }

    private void updateUI(Quote quote) {
        currentQuote = quote;
        //setting font
        Typeface dancingScript = Typeface.createFromAsset(getContext().getAssets(), "fonts/DancingScript-Regular.ttf");
        quote_tv.setTypeface(dancingScript);
        Typeface raleway = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        author_tv.setTypeface(raleway);

        quote_tv.setText(quote.getQuote());
        author_tv.setText(quote.getAuthor());

    }
}
