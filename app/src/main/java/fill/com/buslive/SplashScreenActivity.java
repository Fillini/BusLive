package fill.com.buslive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


/**
 * Created by Fill on 24.10.2015.
 */
public class SplashScreenActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);



        TextView version_tv = (TextView)findViewById(R.id.version_tv);
        version_tv.setText(BuildConfig.VERSION_NAME);


    }
}
