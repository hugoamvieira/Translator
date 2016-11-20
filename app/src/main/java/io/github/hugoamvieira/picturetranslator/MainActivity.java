package io.github.hugoamvieira.picturetranslator;

// PictureTranslator by Hugo Vieira
// Uses Material Camera by Aidan Follestad under the Apache v2.0 License

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

public class MainActivity extends AppCompatActivity {

    private final static int CAMERA_RQ = 6969; // Camera request code
    private final static String TAG = "PictureTranslator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start camera in stillshot mode
        final MaterialCamera camera = new MaterialCamera(MainActivity.this);
        camera.stillShot().start(CAMERA_RQ);
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
