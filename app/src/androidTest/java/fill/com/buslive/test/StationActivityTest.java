package fill.com.buslive.test;

import android.database.Cursor;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;

import com.robotium.solo.Solo;

import fill.com.buslive.R;
import fill.com.buslive.StationActivity;

/**
 * Created by Fill on 27.03.2015.
 */
public class StationActivityTest extends ActivityInstrumentationTestCase2<StationActivity> {


    Solo solo;

    public StationActivityTest() {
        super(StationActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }


    public void testOpen(){
        solo.sleep(10000);

    }

}
