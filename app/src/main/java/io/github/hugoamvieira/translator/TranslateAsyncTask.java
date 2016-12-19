package io.github.hugoamvieira.translator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TranslateAsyncTask extends AsyncTask<String, Void, String> {
    private String translateText;
    private String langTo;
    private String translateBaseURL = "https://translate.google.com/";
    private TextView responseTextView;
    private Context context;

    private URL builtTranslateURL;

    public String getTranslateText() {
        return translateText;
    }

    public void setTranslateText(String _word) {
        this.translateText = _word;
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


    public TranslateAsyncTask(String _word, String _langTo, TextView _responseTextView, Context _context) {
        setTranslateText(_word);
        setLangTo(_langTo);
        setResponseTextView(_responseTextView);
        setContext(_context);

        // Build the URL
        translateBaseURL += "/m?sl=auto&hl=" + getLangTo() + "&q=" + getTranslateText();
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

        if (s == null) {
            Toast.makeText(context, "You need internet access to translate words!", Toast.LENGTH_SHORT).show();
            return;
        }

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateString = df.format(date);

        // Write entry to database
        SQLiteDatabase database = new DBHelper(getContext()).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.Translation.COLUMN_OG_TEXT, getTranslateText());
        values.put(DBContract.Translation.COLUMN_LABEL_TRANSLATION_LANG, getLangTo());
        values.put(DBContract.Translation.COLUMN_TRANSLATION, s);
        values.put(DBContract.Translation.COLUMN_TRANSLATION_DATE, dateString);

        long rowId = database.insert(DBContract.Translation.TABLE_NAME, null, values);

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("Translation", s);
        clipboard.setPrimaryClip(data);

        Toast.makeText(context, "Your translation has been successfully saved and copied to clipboard.", Toast.LENGTH_LONG).show();
    }
}
