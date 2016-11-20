package io.github.hugoamvieira.picturetranslator;

// PictureTranslator by Hugo Vieira
// Uses Material Camera by Aidan Follestad under the Apache v2.0 License

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

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

            // Get Cloud Vision API data for translation


        } else if (data != null) {
            Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
