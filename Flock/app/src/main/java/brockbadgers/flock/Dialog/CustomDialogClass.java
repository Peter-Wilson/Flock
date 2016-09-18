package brockbadgers.flock.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import brockbadgers.flock.MainActivity;
import brockbadgers.flock.R;

/**
 * Created by onyiny-ang on 17/09/16.
 */
public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public MainActivity c;
    public Dialog d;
    public Button yes, no;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = (MainActivity)a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.Value(true);
                c.finish();
                break;
            case R.id.btn_no:
                c.Value(false);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
