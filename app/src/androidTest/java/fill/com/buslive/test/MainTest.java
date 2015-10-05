package fill.com.buslive.test;


import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

import static org.mockito.Mockito.mock;


import fill.com.buslive.MainActivity;
import fill.com.buslive.RoutesActivity;
import fill.com.buslive.fragments.views.RoutesComponent;

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

        solo.clickOnActionBarHomeButton();

        solo.clickInList(1);

        solo.waitForActivity(RoutesActivity.class, 2000);

        RoutesComponent routesComponent = (RoutesComponent) solo.getView("route_view");


        assertNotNull(routesComponent.getCheckedSet());


        solo.clickInList(2);
        solo.clickInList(1);
        solo.clickInList(3);


        solo.sleep(100);

        Log.d("MainTest", routesComponent.getCheckedSet().toString());

        assertEquals(3, routesComponent.getCheckedSet().size());

        solo.clickOnImage(0);

        solo.sleep(3000);


    }
}