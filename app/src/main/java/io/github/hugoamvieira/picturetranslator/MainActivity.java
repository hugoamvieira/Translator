package io.github.hugoamvieira.picturetranslator;

// PictureTranslator by Hugo Vieira
// Uses Material Camera by Aidan Follestad under the Apache v2.0 License

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static int CAMERA_RQ = 6969; // Camera request code
    private final static String TAG = "PictureTranslator";

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
            Log.i(TAG, "Saved to: " + data.getDataString());
            pushToCloudVision(data.getData());

        } else if (data != null) {
            Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void pushToCloudVision(Uri uri) {
        assert uri != null;

        // Get textview to show the labels in. Get progress bar
        TextView labelsTextView = (TextView) findViewById(R.id.labels_textview);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.vision_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setIndeterminate(false);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            CloudVisionAsyncTask cloudVisionAsync = new CloudVisionAsyncTask(bitmap, labelsTextView, progressBar);
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
