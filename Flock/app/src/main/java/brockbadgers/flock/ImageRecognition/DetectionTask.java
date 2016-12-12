package brockbadgers.flock.ImageRecognition;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

import brockbadgers.flock.Helpers.MSFaceServiceClient;
import brockbadgers.flock.MainActivity;
import brockbadgers.flock.R;

/**
 * Created by Peter on 12/11/2016.
 */
public class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
    ProgressDialog dialog;
    Activity activity;
    FacesLoadedCallback callback;

    public DetectionTask(Activity currentActivity, FacesLoadedCallback callback)
    {
        activity = currentActivity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Detecting Face");
        dialog.show();
    }

    @Override
    protected Face[] doInBackground(InputStream... params) {
        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = MSFaceServiceClient.getMSServiceClientInstance();
        try {
            // Start detection.
            return faceServiceClient.detect(
                    params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    false,       /* Whether to return face landmarks */
                    null);
        }  catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Face[] faces) {
        dialog.dismiss();
        callback.onFacesLoaded(faces);
    }
}