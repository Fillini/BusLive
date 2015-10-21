package fill.com.buslive.test;


import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import static org.mockito.Mockito.mock;


import fill.com.buslive.MainActivity;

/**
 * Created by Fill on 26.03.2015.
 */
public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public MainTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
    }

    public void testMain(){




    }
}