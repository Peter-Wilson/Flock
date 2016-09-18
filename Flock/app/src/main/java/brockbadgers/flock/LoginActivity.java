package brockbadgers.flock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.Toast;
import com.google.firebase.database.*;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import brockbadgers.flock.Dialog.NameDialog;
import classes.Person;

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


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isActivated = sharedPref.getBoolean("isActivated",false);

        if(isActivated) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mainIntent);
        }else {

            setContentView(R.layout.activity_login);
            NameDialog name = new NameDialog(this);
           // name.requestWindowFeature(Window.FEATURE_NO_TITLE);
           // name.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //name.setContentView(R.layout.activity_name_dialog);
            name.show();


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
                                    finish();
                                }
                            }, LOAD_DELAY);
                        }
                    }
            );



            sFaceServiceClient = new FaceServiceRestClient("6cbf242786b143e8b3b1eadf70e80b68");

            database = FirebaseDatabase.getInstance().getReference();

            database.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (currFaceId != null) {
                        for (DataSnapshot snapChild : dataSnapshot.getChildren()) {
                            Person p = snapChild.getValue(Person.class);
                            if (!p.getId().equals(currFaceId)) {
                                Log.d(TAG, p.getId());
                                Log.d(TAG, currFaceId);
                                new VerificationTask(p.getId(), currFaceId).execute();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "SOMETHING BAD HAPPENED WHEN A VALUE CHANGED: " + databaseError.getMessage());
                }
            });
        }

    }

    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
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
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mainIntent);

            detect(mBitmap);
        }
    }

    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }

    private class VerificationTask extends AsyncTask<Void, String, VerifyResult> {
        private UUID mFaceId0;
        private UUID mFaceId1;

        public VerificationTask(String mFaceId0, String mFaceId1) {
            this.mFaceId0 = UUID.fromString(mFaceId0);
            this.mFaceId1 = UUID.fromString(mFaceId1);


        }

        @Override
        protected VerifyResult doInBackground(Void... voids) {
            try {
                return sFaceServiceClient.verify(mFaceId0, mFaceId1);
            } catch (Exception e) {
                Log.e(TAG, "" + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(VerifyResult verifyResult) {
            if (verifyResult.isIdentical) {
                Log.d(TAG, "YAY");
            } else {
                Log.d(TAG, "NAY");
            }
        }
    }

    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = sFaceServiceClient;
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
                Person p = new Person(43.471265, -80.542684);
                p.setId(faceId);
                p.setName("Test Person");
                database.child("users").child(p.getId()).setValue(p);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor =sharedPref.edit();
                editor.putBoolean("isActivated",true);
                editor.putString(getString(R.string.user_id), faceId);
                editor.commit();
            }
        }
    }
}
