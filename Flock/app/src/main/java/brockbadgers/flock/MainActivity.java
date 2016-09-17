package brockbadgers.flock;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import classes.Person;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp app = FirebaseApp.getInstance();
        setContentView(R.layout.activity_map);

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

        database = FirebaseDatabase.getInstance().getReference();


        //setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag)).getMapAsync(this);
        }



       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

>>>>>>> e4806b890255b77f8bcc38a360c00d49e4aca1d2
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void onGroupAdd(){
        //when someone tries to add someone to their group. . .
        //This will pop up on their phone
        CustomDialogClass cdd= new CustomDialogClass(this);
        cdd.show();
    }

    public void Value(boolean returnVal)
    {

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ((MapFragment)getFragmentManager().findFragmentById(R.id.mapFrag)).getMapAsync(this);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
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


    public void setUpMap() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true); //show the "current location" button in the top right
        map.setOnInfoWindowClickListener(this); //set on click callback for the info window
        map.clear(); //clear any markers currently on the map
        map.setInfoWindowAdapter(new CustomInfoWindow()); //use the custom info window

        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //Starting location is the current location
        LatLng startingLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

        //Set up starting camera position
        CameraPosition startingPos = new CameraPosition.Builder()
                .target(startingLatLng)
                .zoom(13)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(startingPos));

       /* Person testPerson = new Person(testLat, testLong);

        people = new ArrayList<>();
        ArrayList<MarkerOptions> markersOfPeople = new ArrayList<>();

        people.add(testPerson);

        final Handler h = new Handler();
        final int delay = 3 * 1000;*/

        addPeopleMarkersToMap();



    }

    public void addPeopleMarkersToMap(){


        /*Person a = new Person(testLat,testLong);
        a.setName("Riley");
        a.setId("");
        Person b = new Person(testLat + 0.001,testLong + 0.001);
        b.setName("Peter");
        b.setId(1);*/

       // database.child("users").child(a.getName()).setValue(a);
       // database.child("users").child(b.getName()).setValue(b);

        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Person p = postSnapshot.getValue(Person.class);
                    if (hm.containsKey(p.getId())) {
                        Marker marker = (Marker) hm.get(p.getId());
                        marker.setPosition(new LatLng(p.getLat(), p.getLong())); // Update the marker
                    } else {
                        Marker usersMarker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(p.getLat(), p.getLong()))
                                .title("Name: " + p.getName()));
                        hm.put(p.getId(), usersMarker);
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

    /**
     * The custom info window which displays info about the house
     */
    class CustomInfoWindow extends Activity implements GoogleMap.InfoWindowAdapter{
        boolean doneLoadingImage = false; //if the image has finished downloading
        Marker m;

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            //If a new marker has been selected, the image will not have been loaded
            if(!marker.equals(m)){
                doneLoadingImage = false;
            }
            m = marker;

            //Inflate the layout and get the components
            View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            ImageView infoImage = (ImageView)v.findViewById(R.id.infoImage);
            TextView infoPrice = (TextView)v.findViewById(R.id.infoPrice);
            TextView infoAddress = (TextView)v.findViewById(R.id.infoAddress);




            //Set the two text fields on the info window
            infoPrice.setText("Bum Bum");
            infoAddress.setText("I will be back at 7PM!");

            //Load the image with Picasso, using a generic house picture as a placeholder
            //Picasso.with(getApplicationContext()).placeholder(getResources().getDrawable(R.drawable.ic_star_outline_black_24dp)).into(infoImage, this);

            return v;
        }

        /**
         * When Picasso returns with the valid image
         */

        public void onSuccess() {
            //If the image is not already loaded, reopen the info window to show it
            if(!doneLoadingImage){
                Log.d("Picasso", "Image loaded");
                doneLoadingImage = true;
                m.showInfoWindow();
            }
        }

        /**
         * When Picasso can't find the image
         */

        public void onError() {
            Log.e("Picasso", "Image load failed");
            doneLoadingImage = true;
        }


    }


}
