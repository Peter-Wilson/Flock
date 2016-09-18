package brockbadgers.flock.Services;

import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import brockbadgers.flock.R;
import classes.Person;

/**
 * Created by Peter on 9/17/2016.
 */
@SuppressWarnings("ResourceType")
public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    DatabaseReference database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        database = FirebaseDatabase.getInstance().getReference();
        listener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location){
                //Publish to the DB
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String ref = sharedPref.getString(getString(R.string.user_id), null);

                database.child("users").child(ref).child("lat").setValue(location.getLatitude());
                database.child("users").child(ref).child("long").setValue(location.getLongitude());
                /*Person person = new Person(location.getLatitude(), location.getLongitude());
                person.setId(ref);
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("users").child(person.getId()).setValue(person);*/
                Log.d("Long", ""+location.getLongitude());
                Log.d("Lat", ""+location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }


}
