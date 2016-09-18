package brockbadgers.flock.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import brockbadgers.flock.R;

public class NameDialog extends Dialog {

    Activity a;

    public NameDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.a = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_dialog);

        ImageView green = (ImageView) findViewById(R.id.google_green);
        green.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                    }
                }
        );

        ImageView cyan = (ImageView) findViewById(R.id.google_cyan);
        cyan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                    }
                }
        );

        ImageView azure = (ImageView) findViewById(R.id.google_azure);
        azure.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                    }
                }
        );

        ImageView blue = (ImageView) findViewById(R.id.google_blue);
        blue.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                    }
                }
        );

        ImageView violet = (ImageView) findViewById(R.id.google_violet);
        violet.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
                    }
                }
        );

        ImageView yellow = (ImageView) findViewById(R.id.google_yellow);
        yellow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangeColor(v);
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
