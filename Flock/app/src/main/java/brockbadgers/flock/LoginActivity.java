package brockbadgers.flock;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;

public class LoginActivity extends AppCompatActivity {

    final int LOAD_DELAY = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView login = (ImageView)findViewById(R.id.no_login);
        final LinearLayout loginPanel = (LinearLayout) findViewById(R.id.login_panel);

        final Animation fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
        login.startAnimation(fadeIn);

        final Animation slideUp = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up_layout);
        loginPanel.startAnimation(slideUp);

        Button loginBtn = (Button)findViewById(R.id.login_button);
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Animation zoomin = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom_in);
                        login.startAnimation(zoomin);

                        final Animation slideDown = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_down_layout);
                        loginPanel.startAnimation(slideDown);

                        //delay the new page change
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(mainIntent);
                                LoginActivity.this.finish();
                            }
                        }, LOAD_DELAY);
                    }
                }
        );
    }

}
