package brockbadgers.flock;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import brockbadgers.flock.Helpers.DatabaseHelper;
import brockbadgers.flock.Helpers.GPSHelper;
import brockbadgers.flock.Helpers.MSFaceServiceClient;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import brockbadgers.flock.Dialog.CustomDialogClass;
import brockbadgers.flock.Dialog.DurationDialog;
import brockbadgers.flock.Dialog.NameDialog;
import brockbadgers.flock.ImageRecognition.DetectionTask;
import brockbadgers.flock.ImageRecognition.FacesLoadedCallback;
import brockbadgers.flock.ImageRecognition.VerificationCallback;
import brockbadgers.flock.ImageRecognition.VerificationTask;
import classes.Person;

import com.google.android.gms.common.GooglePlayServicesUtil;

import Firebase.FirebaseCalls;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    GoogleMap map; //the map shown in the map fragment
    GoogleApiClient mGoogleApiClient; //google api client for location services
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    boolean googleClientConnected = false; //if the google api client is connected
    boolean waitForConnect = false; //whether to wait to populate the map until google api client is connected
    Location currentLoc; //the currently used location
    double testLat = 43.476265;
    double testLong = -80.542684;
    HashMap hm = new HashMap();
    ArrayList<Person> people;
    DatabaseReference database;
    boolean done = false;
    boolean tookPicture = false;
    boolean foundFaces = false;
    private String currFaceId;
    private boolean faceAlreadyFound = false;
    SharedPreferences sharedPref;


    ArrayList<String> matchedFaceIdList;
    ArrayList<String> kindaMatchedFaceIdList;
    ArrayList<Double> notMatched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Set up google api client
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(AppIndex.API).build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag)).getMapAsync(this);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchDurationPicker();
            }
        });

        if(sharedPref.getString(getString(R.string.user_id), null) == null && getIntent().hasExtra(getString(R.string.user_id))) {
            //Get the id from the login screen and attemp to verify it
            //TODO: don't progress unless there is a valid or new user_id
            Intent intent = getIntent();
            currFaceId = intent.getStringExtra(getString(R.string.user_id));

            new  LoadExistingUserTask().execute();
        }
        else
        {
            UserVerified();
        }

    }

    public void saveFace(String faceId)
    {
        Person p = new Person(testLat, testLong);
        p.setId(faceId);
        p.setName("Test Person");
        database.child("users").child(p.getId()).setValue(p);
        SharedPreferences.Editor editor =sharedPref.edit();
        editor.putBoolean("isActivated",true);
        editor.putString(getString(R.string.user_id), faceId);
        editor.commit();
    }

    public void UserVerified()
    {
        database = FirebaseDatabase.getInstance().getReference();
        matchedFaceIdList = new ArrayList<>();
        kindaMatchedFaceIdList = new ArrayList<>();
        notMatched = new ArrayList<>();

        if(!runtime_permission()){
            GPSHelper.startGPS(this);
        }

        if(sharedPref.getString(getString(R.string.name), null) == null &&
                (sharedPref.getString(getString(R.string.colour), null) == null))
        {
            NameDialog name = new NameDialog(this);
            name.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            name.show();
        }
    }

    public void onGroupAdd(){
        //when someone tries to add someone to their group. . .
        //This will pop up on their phone
        CustomDialogClass cdd= new CustomDialogClass(this);
        cdd.show();

    }

    public void Value(boolean returnVal)
    {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String ref = sharedPref.getString(getString(R.string.firebase_id), null);
        String[] refs = new String[]{ref};
        try {
            GroupRequest req = new GroupRequest("Peter Wilson",refs,this);
            new NotificationKeyTask().execute(req);
        } catch (Exception e) {
            Log.e("Exception: ", "" + e);
        }



    }

    private class VerificationAlreadyInTask extends AsyncTask<Void, String, VerifyResult> {
        private UUID mFaceId0;
        private UUID mFaceId1;
        private String newId;
        private String currentId;

        public VerificationAlreadyInTask(String mFaceId0, String mFaceId1) {
            newId = mFaceId0;
            currentId = mFaceId1;
            this.mFaceId0 = UUID.fromString(mFaceId0);
            this.mFaceId1 = UUID.fromString(mFaceId1);
        }

        @Override
        protected VerifyResult doInBackground(Void... voids) {
            try {
                VerifyResult v =  MSFaceServiceClient.getMSServiceClientInstance().verify(mFaceId0, mFaceId1);
                if(v.confidence > 0.5 || v.isIdentical) {
                    currFaceId = newId;
                    faceAlreadyFound = true;
                }
                return null;

            } catch (Exception e) {
                Log.e(TAG, "" + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(VerifyResult verifyResult) {
            if (verifyResult.isIdentical && !faceAlreadyFound) {
                Log.d(TAG, "YAY");
            } else {
                Log.d(TAG, "NAY");
            }

        }
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





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // openCameraShowPreview();
                    ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag)).getMapAsync(this);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                break;
            case 100:
            {
                if(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    GPSHelper.startGPS(this);
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

    public void openCameraShowPreview() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    //AFTER the user has taken a pic....
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Get the image.
            Bitmap mBitmap = (Bitmap) extras.get("data");


            //TODO: Open peters date time class.
//            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
//            MainActivity.this.startActivity(mainIntent);

            detect(mBitmap);
        }
    }

    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sharedPref.getString(getString(R.string.user_id), null);
        database.child("users").child(userId).child("accepted").setValue(true);
        tookPicture = true;


        // Start a background task to detect faces in the image.
        new DetectionTask(this, new FacesLoadedCallback() {
            @Override
            public void onFacesLoaded(final Face[] faces) {
                foundFaces = faces.length > 0;
                DatabaseHelper.MatchFacesWithDB(MainActivity.this, faces, database, matchedFaceIdList);
            }
        }).execute(inputStream);
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

    public void LaunchDurationPicker()
    {
        DurationDialog howLong = new DurationDialog(this);
        howLong.show();
    }


    public void setUpMap() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if(checkLocationEnabled() != true) {
            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivity(myIntent);
            return;
        }

            map.setMyLocationEnabled(true); //show the "current location" button in the top right
            map.setOnInfoWindowClickListener(this); //set on click callback for the info window
            map.clear(); //clear any markers currently on the map

            currentLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //Starting location is the current location
            LatLng startingLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

            //Set up starting camera position
            CameraPosition startingPos = new CameraPosition.Builder()
                    .target(startingLatLng)
                    .zoom(13)
                    .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(startingPos));


            addPeopleMarkersToMap();




    }

    public boolean checkLocationEnabled(){
        LocationManager locationManager = null;
        boolean gps_enabled= false,network_enabled = false;

        if(locationManager == null)
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){

        }
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){

        }
        if(network_enabled == false || gps_enabled == false){
            return false;
        }else{
            return true;
        }

    }

    public void addPeopleMarkersToMap(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
       final  String userId = sharedPref.getString(getString(R.string.user_id), null);

        database.child("users").child(userId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(done){
                    for(String faceId : matchedFaceIdList){
                        database.child("users").child(faceId).child("group").setValue(1);
                    }
                }


               if(dataSnapshot.getKey().equals("group")){
                   Long value = (Long) dataSnapshot.getValue();
                   if(value != 0 && !tookPicture ){
                       onGroupAdd();
                   }
               }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Database cancel error :",databaseError.getMessage());
            }
        });



        database.child("users").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot snapshot) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.super.getApplicationContext());
                String userId = sharedPref.getString(getString(R.string.user_id), null);
                int myGroup = sharedPref.getInt("myGroup",-1);

                float[] colours = { BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_AZURE,  BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_VIOLET /* etc */ };

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Person p = postSnapshot.getValue(Person.class);
                    if(!p.getId().equals(userId) && p.getGroup() == 1) {
                        if (hm.containsKey(p.getId())) {

                            Marker marker = (Marker) hm.get(p.getId());
                            marker = map.addMarker(new MarkerOptions()
                                    .position(new LatLng(p.getLat(), p.getLong()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(colours[p.getColour()])));
                        } else {
                            Marker usersMarker = map.addMarker(new MarkerOptions()
                                    .position(new LatLng(p.getLat(), p.getLong()))
                                    .title(p.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(colours[p.getColour()])));
                            hm.put(p.getId(), usersMarker);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Database cancel error :",databaseError.getMessage());
            }
        });


   }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(googleClientConnected) {
            //If the google client is already connected, load the layout now
            setUpMap();
        } else {
            //Otherwise wait for it to finish connecting
            waitForConnect = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleClientConnected = true;

        //If the map finished setting up before the connection was established, load the data now
        if(waitForConnect){
            waitForConnect = false;
            setUpMap();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("GoogleClientApi", connectionResult.getErrorMessage());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://brockbadgers.flock/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://brockbadgers.flock/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }

    private class LoadExistingUserTask extends AsyncTask<String, String, String> {
        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Verifying Face with Database");
            dialog.show();
        }

        //Determine if that user is already in the DB
        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = MSFaceServiceClient.getMSServiceClientInstance();
            try {
                Query queryRef = database.orderByKey();
                //This shouldn't be on Change, we want to call it before we input it into the db
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
                        if (currFaceId != null) {
                            for (DataSnapshot snapChild : dataSnapshot.getChildren()) {
                                Person p = snapChild.getValue(Person.class);
                                if (!p.getId().equals(currFaceId)) {
                                    Log.d(TAG, p.getId());
                                    Log.d(TAG, currFaceId);
                                    new VerificationAlreadyInTask(p.getId(), currFaceId).execute();
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "SOMETHING BAD HAPPENED WHEN A VALUE CHANGED: " + databaseError.getMessage());
                    }
                });
                return null;
            }  catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String string) {
                dialog.hide();
                if(!faceAlreadyFound)
                    saveFace(currFaceId);
                UserVerified();
            }
        }





    class NotificationKeyTask extends AsyncTask<GroupRequest, Void, String> {

        @Override
        protected String doInBackground(GroupRequest... params) {
            GroupRequest param = params[0];
            try {
                return new FirebaseCalls().addtNotificationKey(param.getLeaderName(), param.getRequestIds(), param.getCtx());
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


}




