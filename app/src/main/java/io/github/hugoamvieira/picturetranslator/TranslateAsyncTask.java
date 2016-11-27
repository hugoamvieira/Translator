package io.github.hugoamvieira.picturetranslator;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

public class TranslateAsyncTask extends AsyncTask<String, Void, String> {
    private String word;
    private String langTo;
    private String translateBaseURL = "https://translate.google.com/";
    private TextView responseTextView;

    private URL builtTranslateURL;

    public String getWord() {
        return word;
    }

    public void setWord(String _word) {
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

    public TranslateAsyncTask(String _word, String _langTo, TextView _responseTextView) {
        setWord(_word);
        setLangTo(_langTo);
        setResponseTextView(_responseTextView);

        // Build the URL
        translateBaseURL += "/m?sl=en&hl=" + getLangTo() + "&q=" + getWord();
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
        responseTextView.setText(s);
    }
}
