package brockbadgers.flock.Helpers;

import android.app.Activity;
import android.content.Intent;

import brockbadgers.flock.Services.GPS_Service;

/**
 * Created by Peter on 12/11/2016.
 */
public class GPSHelper {
    public static void startGPS(Activity a)
    {
        Intent i = new Intent(a.getApplicationContext(), GPS_Service.class);
        a.startService(i);
    }

    public static void stopGPS(Activity a)
    {
        Intent i = new Intent(a.getApplicationContext(), GPS_Service.class);
        a.stopService(i);
    }
}
