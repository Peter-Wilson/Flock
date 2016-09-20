package brockbadgers.flock.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import brockbadgers.flock.R;

public class NameDialog extends Dialog {

    Activity a;
    int colour = 0;

    public NameDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.a = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_dialog);

        Button submit = (Button) findViewById(R.id.submit_name);
        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
                        String ref = sharedPref.getString(a.getString(R.string.user_id), null);
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        String name = ((EditText)a.findViewById(R.id.Name)).getText().toString();
                        database.child("users").child(ref).child("name").setValue(name);
                        database.child("users").child(ref).child("colour").setValue(colour);


                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(a.getString(R.string.name), name);
                        editor.putInt(a.getString(R.string.colour), colour);
                        editor.commit();
                        hide();
                    }
                }
        );

        ImageView green = (ImageView) findViewById(R.id.google_green);
        green.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                        colour = 0;
                    }
                }
        );

        ImageView cyan = (ImageView) findViewById(R.id.google_cyan);
        cyan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                        colour = 1;
                    }
                }
        );

        ImageView azure = (ImageView) findViewById(R.id.google_azure);
        azure.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                        colour = 2;
                    }
                }
        );

        ImageView blue = (ImageView) findViewById(R.id.google_blue);
        blue.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                        colour = 3;
                    }
                }
        );

        ImageView violet = (ImageView) findViewById(R.id.google_violet);
        violet.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                        colour = 4;
                    }
                }
        );

        ImageView yellow = (ImageView) findViewById(R.id.google_yellow);
        yellow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                        colour = 5;
                    }
                }
        );

    }

    void ChangeColor(View v)
    {
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.background_name);
        switch(v.getId())
        {
            case R.id.google_green:
                layout.setBackground(a.getDrawable(R.drawable.rounded_green));
                break;
            case R.id.google_cyan:
                layout.setBackground(a.getDrawable(R.drawable.rounded_cyan));
                break;
            case R.id.google_azure:
                layout.setBackground(a.getDrawable(R.drawable.rounded_azure));
                break;
            case R.id.google_blue:
                layout.setBackground(a.getDrawable(R.drawable.rounded_blue));
                break;
            case R.id.google_violet:
                layout.setBackground(a.getDrawable(R.drawable.rounded_violet));
                break;
            case R.id.google_yellow:
                layout.setBackground(a.getDrawable(R.drawable.rounded_yellow));
                break;

        }
    }

}
