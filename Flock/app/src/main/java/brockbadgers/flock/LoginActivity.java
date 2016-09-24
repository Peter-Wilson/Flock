package brockbadgers.flock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;

import brockbadgers.flock.Helpers.MSFaceServiceClient;
import com.google.firebase.database.*;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    final int LOAD_DELAY = 800;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String TAG = LoginActivity.class.getName();

    DatabaseReference database;

    private static FaceServiceClient sFaceServiceClient;
    private String currFaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isActivated = sharedPref.getBoolean("isActivated", false);

        if(isActivated) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mainIntent);
        }else {

            setContentView(R.layout.activity_login);


            final ImageView login = (ImageView) findViewById(R.id.no_login);
            final LinearLayout loginPanel = (LinearLayout) findViewById(R.id.login_panel);

            final Animation fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
            login.startAnimation(fadeIn);

            final Animation slideUp = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up_layout);
            loginPanel.startAnimation(slideUp);

            reqCamPermissions();

            Button loginBtn = (Button) findViewById(R.id.login_button);
            loginBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Animation zoomin = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom_in);
                            login.startAnimation(zoomin);

                            final Animation slideDown = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_down_layout);
                            loginPanel.startAnimation(slideDown);

                            //delay the new page change
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    openCameraShowPreview();
                                }
                            }, LOAD_DELAY);
                        }
                    }
            );

            sFaceServiceClient = new FaceServiceRestClient("6cbf242786b143e8b3b1eadf70e80b68");
        }

    }

    public void reqCamPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //
                    Log.d(TAG, "DENIED");
                }
        }
    }


    private void openCameraShowPreview() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /* Create an Intent that will start the Menu-Activity. */
            Bundle extras = data.getExtras();
            Bitmap mBitmap = (Bitmap) extras.get("data");
            detect(mBitmap);
        }
    }

    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }



    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(LoginActivity.this);
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
            String faceId = faces[0].faceId.toString();
            if (faceId != null) {
                currFaceId = faceId;
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainIntent.putExtra(getString(R.string.user_id), currFaceId);
                LoginActivity.this.startActivity(mainIntent);
                finish();
            }
        }
    }
}
