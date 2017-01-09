package io.github.hugoamvieira.translator;

// Translator by Hugo Vieira

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


public class MainActivity extends AppCompatActivity {

    private Context context = this;
    protected final static String TAG = "translator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get UI elements
        final Button translationHistBtn = (Button) findViewById(R.id.translation_history_btn);
        final Button pasteFromClipboardBtn = (Button) findViewById(R.id.paste_clipboard_btn);
        final EditText textToTranslateEditText = (EditText) findViewById(R.id.text_to_translate);
        final Spinner translateLangs = (Spinner) findViewById(R.id.languages_spinner);


        translationHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(i);
            }
        });


        pasteFromClipboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                try {
                    ClipData.Item data = clipboard.getPrimaryClip().getItemAt(0);

                    if (!clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                        Toast.makeText(context, "Data in clipboard is not usable in this context.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Put contents into edit text
                    textToTranslateEditText.setText(data.getText());

                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    Log.e(TAG, "There's no data in clipboard");
                    Toast.makeText(context, "There's no data in the clipboard!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        translateLangs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get text from edit text
                String textToTranslate = "";

                if (!textToTranslateEditText.getText().toString().isEmpty()) {
                    textToTranslate = textToTranslateEditText.getText().toString();
                }

                // Get text view where the response is going to be put
                final TextView responseTextView = (TextView) findViewById(R.id.result_text);

                String langTo = "";
                switch (i) {
                    case 1: // To PT
                        langTo = "pt";
                        break;

                    case 2: // To ES
                        langTo = "es";
                        break;

                    case 3: // To FR
                        langTo = "fr";
                        break;

                    case 4: // To DE
                        langTo = "de";
                        break;

                    case 5: // To EN
                        langTo = "en";
                        break;

                    case 6: // To RU
                        langTo = "ru";
                        break;

                    default:
                        break;
                }

                if (!langTo.isEmpty()) {
                    TranslateAsyncTask translate = new TranslateAsyncTask(textToTranslate, langTo, responseTextView, context);
                    translate.execute();
                }

                // Set the spinner selection back to the "Select a language..." option
                translateLangs.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }
}
