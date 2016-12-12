package brockbadgers.flock.ImageRecognition;

import com.microsoft.projectoxford.face.contract.VerifyResult;

/**
 * Created by Peter on 12/11/2016.
 */
public interface VerificationCallback {
    void VerificationResult(VerifyResult verifyResult);
}
