package brockbadgers.flock.ImageRecognition;

import com.microsoft.projectoxford.face.contract.Face;

/**
 * Created by Peter on 12/11/2016.
 */
public interface FacesLoadedCallback {
        void onFacesLoaded(Face[] faces);
}
