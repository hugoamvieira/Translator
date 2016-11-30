package io.github.hugoamvieira.translator;

// Translator by Hugo Vieira

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Context context = this;
    protected final static String TAG = "translator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get UI elements
        Button translationHistBtn = (Button) findViewById(R.id.translation_history_btn);

        translationHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(i);
            }
        });


        final Spinner translateLangs = (Spinner) findViewById(R.id.languages_spinner);
        translateLangs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get text from edit text
                EditText textToTranslateEditText = (EditText) findViewById(R.id.text_to_translate);
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
