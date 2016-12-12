package brockbadgers.flock.Helpers;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class MSFaceServiceClient {

    private static final String MSAPI = "40a127215baa4c9382013fe624369fed";

    public static FaceServiceClient getMSServiceClientInstance() {
        return new FaceServiceRestClient(MSAPI);
    }
}
