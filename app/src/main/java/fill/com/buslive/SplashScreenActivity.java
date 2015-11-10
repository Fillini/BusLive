package fill.com.buslive;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationSet;
import android.widget.TextView;


/**
 * Created by Fill on 24.10.2015.
 */
public class SplashScreenActivity extends AppCompatActivity {

    Handler handler = new Handler();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        textView = (TextView)findViewById(R.id.textView);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);



        TextView version_tv = (TextView)findViewById(R.id.version_tv);
        version_tv.setText("ver. " + BuildConfig.VERSION_NAME);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0, 1);
        alphaAnimator.setStartDelay(500);
        alphaAnimator.setDuration(700);
        alphaAnimator.start();


    }
}
