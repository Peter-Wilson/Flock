package brockbadgers.flock.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.Calendar;

import brockbadgers.flock.MainActivity;
import brockbadgers.flock.R;

public class LoadingDialog extends Dialog {


    public LoadingDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
    }
}
