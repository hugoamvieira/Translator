package io.github.hugoamvieira.picturetranslator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class CloudVisionAsyncTask extends AsyncTask<String, Integer, Label> {
    private static final String API_KEY = "";

    private Bitmap bitmapImg;
    private EditText resultEditText;
    private ProgressBar visionProgressBar;
    private Context context;

    public CloudVisionAsyncTask(Bitmap image, EditText resultEditText, ProgressBar progressBar, Context context) {
        this.bitmapImg = image;
        this.resultEditText = resultEditText;
        this.visionProgressBar = progressBar;
        this.context = context;
    }

    @Override
    protected Label doInBackground(String... strings) {
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            // Build the vision with the http transport, json factory and the API Key
            Vision.Builder visionBuilder = new Vision.Builder(httpTransport, jsonFactory, null);
            visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer(API_KEY));
            Vision vision = visionBuilder.build();

            Log.d(MainActivity.TAG, "Vision builder created with API Key");

            // Create a batch request
            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
            batchRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                AnnotateImageRequest imageRequest = new AnnotateImageRequest();
                Image image = new Image();

                // Compress the image to JPEG
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapImg.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Encode JPEG
                image.encodeContent(imageBytes);
                imageRequest.setImage(image);

                // Tell Cloud Vision what we want. In this case, Label Detection
                imageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature feature = new Feature();
                    feature.setType("LABEL_DETECTION");
                    feature.setMaxResults(3);
                    add(feature);
                }});

                // Add the list to the image request
                add(imageRequest);
            }});

            Log.d(MainActivity.TAG, "Image parsed and ready to go");

            Vision.Images.Annotate annotateReq = vision.images().annotate(batchRequest);
            annotateReq.setDisableGZipContent(true);

            // Get the response from cloud vision and return it as a string
            BatchAnnotateImagesResponse response = annotateReq.execute();

            Log.d(MainActivity.TAG, "Got response");

            return getHighestConfidenceLabel(response);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, "Failed due to: " + e.getMessage());
        }

        return null;
    }

    protected void onPostExecute(Label result) {
        if (result == null) {
            Toast.makeText(context, "There was a problem accessing the Vision API", Toast.LENGTH_SHORT).show();
            return;
        }

        super.onPostExecute(result);

        // How to pass the label components back to main:
        resultEditText.setText(result.getLabelName());
        // confidenceEditText.setText(result.getConfidence());

        // And then in main, get the text on both ETs and create Label object :)

        visionProgressBar.setVisibility(View.INVISIBLE);
    }


    // Converts all the labels returned by cloud vision API to a string
    private Label getHighestConfidenceLabel(BatchAnnotateImagesResponse response) {
        List<EntityAnnotation> imageLabels = response.getResponses().get(0).getLabelAnnotations();

        // If it can't find anything, return a message
        if (imageLabels == null) return null;

        ArrayList<Label> labels = new ArrayList<>();
        // Add the labels and the confidence % for each
        for (EntityAnnotation label : imageLabels) {
            labels.add(new Label(label.getConfidence(), label.getDescription()));
        }

        Label highestConfidenceLabel = new Label(0, "");
        float confidence = 0f;

        // Get the label with the highest confidence
        for (Label l : labels) {
            if (l.getConfidence() > confidence) {
                confidence = l.getConfidence();
                highestConfidenceLabel = l;
            }
        }

        return highestConfidenceLabel;
    }
}
