package Firebase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import brockbadgers.flock.GroupRequest;
import brockbadgers.flock.LoginActivity;
import brockbadgers.flock.R;

/**
 * Created by Peter on 9/17/2016.
 */
public class FirebaseCalls {

    static final String TAG = FirebaseCalls.class.getCanonicalName();

    // This snippet takes the simple approach of using the first returned Google account,
// but you can pick any Google account on the device.
    public static String getAccount(Context ctx) {
        Account[] accounts = AccountManager.get(ctx).
                getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0].name;
    }


    //Add Notification Key
    public static String addtNotificationKey(String leaderName, String[] registrationId, Context ctx)
            throws IOException, JSONException {

        String accountName = getAccount(ctx);

        // Initialize the scope using the client ID you got from the Console.
        final String scope = "audience:server:client_id:"
                + "1262xxx48712-9qs6n32447mcj9dirtnkyrejt82saa52.apps.googleusercontent.com";
        String idToken = null;
        try {
            idToken = GoogleAuthUtil.getToken(ctx, accountName, scope);
        } catch (Exception e) {
            Log.d("A","exception while getting idToken: " + e);
        }

        URL url = new URL("https://fcm.googleapis.com/fcm/send");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("Authorization", "key = AIzaSyD31ROe_ALwLl4h7xrDhZ6UtV3QegtLx1U");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("project_id", "702826245822");
        con.connect();


        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "create");
        data.put("notification_key_name", leaderName);
        data.put("registration_ids", registrationId);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        int status = con.getResponseCode();
        Log.e("google link", con.getErrorStream().toString());

        // Read the response into a string
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");
    }



    //Add Users to the group
    public static void addUsersToGroup(String id, String[] userKey,
           String senderId, String notificationKey, String registrationId, String idToken) throws IOException, JSONException{
        URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "add");
        data.put("notification_key", notificationKey);
        data.put("registration_ids", new JSONArray(Arrays.asList(userKey)));

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        Log.d(TAG, response.getString(responseString));
    }

    //Remove Users from the group
    private void removeUsersFromGroup(String id, String[] userKey,
              String senderId, String notificationKey, String registrationId, String idToken) throws IOException, JSONException{
        URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "add");
        data.put("notification_key_name", notificationKey);
        data.put("registration_ids", new JSONArray(Arrays.asList(userKey)));

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        Log.d(TAG, response.getString(responseString));
    }

}
