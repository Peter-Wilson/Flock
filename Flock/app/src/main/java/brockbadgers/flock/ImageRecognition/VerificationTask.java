package brockbadgers.flock.ImageRecognition;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.util.ArrayList;
import java.util.UUID;

import brockbadgers.flock.Helpers.MSFaceServiceClient;

/**
 * Created by Peter on 12/11/2016.
 */
public class VerificationTask extends AsyncTask<Void, String, VerifyResult> {
    private final String TAG = VerificationTask.class.getCanonicalName();
    ArrayList<String> matchedFaceIdList;
    VerificationCallback callback;
    private UUID mFaceId0;
    private UUID mFaceId1;

    public VerificationTask(String mFaceId0, String mFaceId1, ArrayList<String> matchedFaceList, VerificationCallback callback) {
        this.mFaceId0 = UUID.fromString(mFaceId0);
        this.mFaceId1 = UUID.fromString(mFaceId1);
        this.matchedFaceIdList = matchedFaceList;
        this.callback = callback;
    }

    @Override
    protected VerifyResult doInBackground(Void... voids) {
        try {
            VerifyResult v =  MSFaceServiceClient.getMSServiceClientInstance().verify(mFaceId0, mFaceId1);
            if(v != null && v.confidence > 0.5 || v.isIdentical) {
                matchedFaceIdList.add(mFaceId1.toString());
            }
            return v;
        } catch (Exception e) {
            String error = e.getMessage();
            Log.e(TAG, "" + error);
            return null;
        }
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "VERIFY RESULT WAS CANCELLED");
    }

    @Override
    protected void onPostExecute(VerifyResult verifyResult) {
        Log.d("We are verifying", "Now");
        callback.VerificationResult(verifyResult);
    }
}
