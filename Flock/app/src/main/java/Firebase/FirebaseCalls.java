package Firebase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DownloadManager;
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
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Peter on 9/17/2016.
 */
public class FirebaseCalls {

    static final String TAG = FirebaseCalls.class.getCanonicalName();


    //Add Notification Key
    public String addtNotificationKey(String leaderName, String[] registrationId, Context ctx)
            throws IOException, JSONException {

        URL url = new URL("https://fcm.googleapis.com/fcm/send");


        JSONObject json = new JSONObject();
        json.put("operation","create");
        json.put("registration_ids", new JSONArray(registrationId));
        json.put("notification_key_name", "peterWilson");


        String responseString = post(url.toString(), json.toString());
        JSONObject response = new JSONObject(responseString);
        Log.d(TAG,responseString);
        return response.getString("notification_key");
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    Headers buildHeaders()
    {
        return new Headers.Builder()
                .add("Authorization","key=AIzaSyCSt1P5ckEfVNG1ikLR78lR4MfvabJvfYo")
                .add("Content-Type","application/json")
                .add("project_id","flock-b958e")
                .build();
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders())
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
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
