package io.github.hugoamvieira.picturetranslator;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.Locale;

public class CloudVisionAsyncTask extends AsyncTask<Object, Integer, String> {
    private static final String API_KEY = "";
    private final static String TAG = "PictureTranslator";

    private Bitmap bitmapImg;
    private TextView resultTextView;
    private ProgressBar visionProgressBar;

    public CloudVisionAsyncTask(Bitmap image, TextView textView, ProgressBar progressBar) {
        this.bitmapImg = image;
        this.resultTextView = textView;
        this.visionProgressBar = progressBar;
    }

    @Override
    protected String doInBackground(Object... objects) {
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            // Build the vision with the http transport, json factory and the API Key
            Vision.Builder visionBuilder = new Vision.Builder(httpTransport, jsonFactory, null);
            visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer(API_KEY));
            Vision vision = visionBuilder.build();

            Log.d(TAG, "Vision builder created with API Key");

            // Create a batch request
            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
            batchRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                AnnotateImageRequest imageRequest = new AnnotateImageRequest();
                Image image = new Image();

                // Compress the image to JPEG
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapImg.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
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

            Log.d(TAG, "Image parsed and ready to go");

            Vision.Images.Annotate annotateReq = vision.images().annotate(batchRequest);
            annotateReq.setDisableGZipContent(true);

            // Get the response from cloud vision and return it as a string
            BatchAnnotateImagesResponse response = annotateReq.execute();

            Log.d(TAG, "Got response");

            return labelsToString(response);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed due to: " + e.getMessage());
        }

        return "Cloud Vision API request failed. This incident has been logged.";
    }

    protected void onPostExecute(String result) {
        resultTextView.setText(result);
        visionProgressBar.setVisibility(View.INVISIBLE);
    }


    // Converts all the labels returned by cloud vision API to a string
    private String labelsToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();
        message.append("I found these things:\n\n");

        List<EntityAnnotation> imageLabels = response.getResponses().get(0).getLabelAnnotations();

        // If it can't find anything, return a message saying that nothing was found
        if (imageLabels == null) message.append("nothing");

        // Append the labels and the confidence % for each
        for (EntityAnnotation label : imageLabels) {
            message.append(String.format(Locale.US, "%.2f: %s", label.getScore(), label.getDescription()));
            message.append("\n");
        }

        return message.toString();
    }
}
