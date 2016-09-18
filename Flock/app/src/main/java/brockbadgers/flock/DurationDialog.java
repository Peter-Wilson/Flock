package brockbadgers.flock;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DurationDialog extends Dialog {

    public Activity activity;
    public Dialog d;

    public DurationDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_date_time_dialog);


        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(true);

        Button submit = (Button) findViewById(R.id.submitTime);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
                int hours = np.getValue();

                Calendar now = Calendar.getInstance();
                now.add(Calendar.HOUR,hours);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat(activity.getString(R.string.hour_duration), now.getTimeInMillis());
                editor.commit();
                hide();
            }
        });

    }
}
