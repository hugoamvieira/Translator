package io.github.hugoamvieira.picturetranslator;

// PictureTranslator by Hugo Vieira
// Uses Material Camera by Aidan Follestad under the Apache v2.0 License

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static boolean HAVE_BILLING = false; // Means I don't have a Billing method to add to my GCP account..
    private final static int CAMERA_RQ = 6969; // Camera request code
    private Context context = this;
    protected final static String TAG = "picTrans";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get UI elements
        Button takePictureBtn = (Button) findViewById(R.id.take_picture_btn);
        Button translationHistBtn = (Button) findViewById(R.id.translation_history_btn);

        // Set OnClickListeners for buttons
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start camera in stillshot mode
                final MaterialCamera camera = new MaterialCamera(MainActivity.this);
                camera.stillShot().start(CAMERA_RQ);
            }
        });

        translationHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != CAMERA_RQ) return;

        if (resultCode == RESULT_OK) {
            RelativeLayout resultsLayout = (RelativeLayout) findViewById(R.id.result_layout);
            resultsLayout.setVisibility(View.VISIBLE);

            Log.i(TAG, "Saved to: " + data.getDataString());

            // I don't have billing to enable Vision API
            if (HAVE_BILLING)
                pushToCloudVision(data.getData());

            // This simulates the response that I would get from Cloud Vision Async
            final Label response = new Label(97.5f, "Car");

            EditText wordToTranslate = (EditText) findViewById(R.id.text_to_translate);
            wordToTranslate.setText(response.getLabelName());

            // Get text view where the response is going to be put
            final TextView responseTextView = (TextView) findViewById(R.id.result_text);

            // Get the language to translate to
            Spinner translateLangs = (Spinner) findViewById(R.id.languages_spinner);
            translateLangs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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

                        default:
                            break;
                    }

                    if (!langTo.isEmpty()) {
                        TranslateAsyncTask translate = new TranslateAsyncTask(response, langTo, responseTextView, context);
                        translate.execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do nothing
                }
            });


        } else if (data != null) {
            Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void pushToCloudVision(Uri uri) {
        assert uri != null;

        // Get textview to show the labels in. Get progress bar
        EditText labelEditText = (EditText) findViewById(R.id.text_to_translate);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setIndeterminate(false);

        try {
            Bitmap bitmapFull = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            // Scale the image down by half
            Bitmap bitmap = bitmapFull.createScaledBitmap(bitmapFull, bitmapFull.getWidth() / 2, bitmapFull.getHeight() / 2, false);

            CloudVisionAsyncTask cloudVisionAsync = new CloudVisionAsyncTask(bitmap, labelEditText, progressBar);
            cloudVisionAsync.execute();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed due to: " + e.getMessage());

            // Output something for the user
            Toast.makeText(this, "Image was not found", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed due to: " + e.getMessage());

            // Output something for the user
            Toast.makeText(this, "Error in parsing image", Toast.LENGTH_SHORT).show();
        }
    }
}
