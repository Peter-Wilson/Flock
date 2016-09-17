package brockbadgers.flock;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import Firebase.FirebaseCalls;
import brockbadgers.flock.Services.GPS_Service;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mImageView = (ImageView) findViewById(R.id.image_thumbnail);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String ref = sharedPref.getString(getString(R.string.firebase_id), null);
        String[] refs = new String[]{ref};
        try {
            GroupRequest req = new GroupRequest("Peter Wilson",refs,this);
            new NotificationKeyTask().execute(req);
        } catch (Exception e) {
            Log.e("Exception: ", "" + e);
        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            reqCamPermissions();
        } else {
            openCameraShowPreview();
        }

        if(runtime_permission())
            startGPS();

    }

    public void startGPS()
    {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
    }

    public void stopGPS()
    {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        stopService(i);
    }

    private boolean runtime_permission() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission
        .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }



    public void reqCamPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraShowPreview();
                } else {
                    //
                    Log.d(TAG, "DENIED");
                }
                break;
            case 100:
            {
                if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    startGPS();
                }else{
                    runtime_permission();
                }
                break;
            }
        }
    }

    private boolean checkGooglePlayServicesAvailable()
    {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
        {
            return true;
        }

        Log.e("MAIN ACTIVITY", "Google Play Services not available: " + GooglePlayServicesUtil.getErrorString(status));

        if (GooglePlayServicesUtil.isUserRecoverableError(status))
        {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            if (errorDialog != null)
            {
                errorDialog.show();
            }
        }

        return false;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void openCameraShowPreview() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class NotificationKeyTask extends AsyncTask<GroupRequest, Void, String> {

    @Override
    protected String doInBackground(GroupRequest... params) {
        GroupRequest param = params[0];
        try {
            return FirebaseCalls.addtNotificationKey(param.getLeaderName(), param.getRequestIds(), param.getCtx());
        } catch (Exception e) {
            Log.e("Exception", "" + e);
            return "No";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(FirebaseCalls.class.getName(), s);
    }
}
