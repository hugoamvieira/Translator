package io.github.hugoamvieira.picturetranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TranslateAsyncTask extends AsyncTask<String, Void, String> {
    private Label word;
    private String langTo;
    private String translateBaseURL = "https://translate.google.com/";
    private TextView responseTextView;
    private Context context;

    private URL builtTranslateURL;

    public Label getWord() {
        return word;
    }

    public void setWord(Label _word) {
        this.word = _word;
    }

    public String getLangTo() {
        return langTo;
    }

    public void setLangTo(String _langTo) {
        this.langTo = _langTo;
    }

    public TextView getResponseTextView() {
        return responseTextView;
    }

    public void setResponseTextView(TextView _responseTextView) {
        this.responseTextView = _responseTextView;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context _context) {
        this.context = _context;
    }

    public TranslateAsyncTask(Label _word, String _langTo, TextView _responseTextView, Context _context) {
        setWord(_word);
        setLangTo(_langTo);
        setResponseTextView(_responseTextView);
        setContext(_context);

        // Build the URL
        translateBaseURL += "/m?sl=en&hl=" + getLangTo() + "&q=" + getWord().getLabelName();
        try {
            builtTranslateURL = new URL(translateBaseURL);
            Log.d(MainActivity.TAG, builtTranslateURL.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Connection conn = Jsoup.connect(builtTranslateURL.toString());
            conn.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36");
            Document doc = conn.get().normalise();

            String result = doc.getElementsByClass("t0").text();
            Log.d(MainActivity.TAG, result);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        getResponseTextView().setText(s);

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateString = df.format(date);

        // Write entry to database
        SQLiteDatabase database = new DBHelper(getContext()).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.Translation.COLUMN_LABEL_NAME, getWord().getLabelName());
        values.put(DBContract.Translation.COLUMN_LABEL_CONFIDENCE, getWord().getConfidence());
        values.put(DBContract.Translation.COLUMN_LABEL_TRANSLATION_LANG, getLangTo());
        values.put(DBContract.Translation.COLUMN_TRANSLATION, s);
        values.put(DBContract.Translation.COLUMN_TRANSLATION_DATE, dateString);

        long rowId = database.insert(DBContract.Translation.TABLE_NAME, null, values);

        Toast.makeText(context, "Your translation has been successfully saved. ID: " + rowId, Toast.LENGTH_SHORT).show();
    }
}
