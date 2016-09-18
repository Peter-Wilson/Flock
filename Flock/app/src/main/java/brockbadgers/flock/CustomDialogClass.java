package brockbadgers.flock;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by onyiny-ang on 17/09/16.
 */
public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public MainActivity c;
    public Dialog d;
    public Button yes, no;
    DatabaseReference database;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = (MainActivity)a;
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String userId = sharedPref.getString(c.getString(R.string.user_id), null);
        switch (v.getId()) {
            case R.id.btn_yes:
                database.child("users").child(userId).child("accepted").setValue(true);
                c.Value(true);
                dismiss();
                break;
            case R.id.btn_no:
                database.child("users").child(userId).child("group").setValue(0);
                c.Value(false);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
