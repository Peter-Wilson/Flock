package brockbadgers.flock.Helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import brockbadgers.flock.ImageRecognition.VerificationCallback;
import brockbadgers.flock.ImageRecognition.VerificationTask;
import brockbadgers.flock.R;

/**
 * Created by Peter on 12/11/2016.
 */
public class DatabaseHelper {

    public static void MatchFacesWithDB(final Activity a, final Face[] faces, DatabaseReference database, final ArrayList<String> matchedFaceIdList)
    {
        //Detect faces and add them to the matchedFaceList
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Face face : faces) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
                    String ref = sharedPref.getString(a.getString(R.string.user_id), null);

                    for (DataSnapshot snapChild : dataSnapshot.getChildren()) {
                        HashMap<String, String> dbMap = (HashMap<String, String>) snapChild.getValue();
                        //DB Values.
                        Set<String> dbKey = dbMap.keySet();
                        for (String dbFaceId : dbKey) {
                            if (!face.faceId.toString().equals(ref)) {
                                new VerificationTask(face.faceId.toString(), dbFaceId, matchedFaceIdList, new VerificationCallback() {
                                    @Override
                                    public void VerificationResult(VerifyResult verifyResult) {

                                    }
                                }).execute();
                            }
                        }
                    }
                }

                //alert if no new matches
                if (matchedFaceIdList.isEmpty()) {
                    if (faces.length > 0) {
                        Toast.makeText(a.getApplicationContext(), "No faces found.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(a.getApplicationContext(), "No results found, please make an account! It's pretty easy", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
