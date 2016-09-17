package brockbadgers.flock;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;

public class LoginActivity extends AppCompatActivity {

    final int LOAD_DELAY = 800;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView login = (ImageView)findViewById(R.id.no_login);
        final LinearLayout loginPanel = (LinearLayout) findViewById(R.id.login_panel);

        final Animation fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
        login.startAnimation(fadeIn);

        final Animation slideUp = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up_layout);
        loginPanel.startAnimation(slideUp);

        Button loginBtn = (Button)findViewById(R.id.login_button);
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
                    openCameraShowPreview();
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
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mainIntent);
        }
    }

}
