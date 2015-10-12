package fill.com.buslive;

import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;

import android.view.Menu;

import android.view.MenuItem;
import android.widget.ImageView;

import fill.com.buslive.fragments.views.DottedProgressBar;


public class StationActivity extends AppCompatActivity {


    ImageView animation_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        animation_iv = (ImageView)findViewById(R.id.animation_iv);


        DottedProgressBar dottedProgressBar = new DottedProgressBar();
        animation_iv.setImageDrawable(dottedProgressBar);
        dottedProgressBar.start();



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
