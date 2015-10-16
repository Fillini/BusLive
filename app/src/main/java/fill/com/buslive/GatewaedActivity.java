package fill.com.buslive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import fill.com.buslive.fragments.RoutesFragment;
import fill.com.buslive.http.GeocodingGateway;
import fill.com.buslive.http.PeriodicGateway;
import fill.com.buslive.http.ResponseCallback;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.utils.SPHelper;


public class GatewaedActivity extends AppCompatActivity implements
        ResponseCallback {

    GeocodingGateway geocodingGateway;
    PeriodicGateway periodicGateway;
    ServerGateway gateway;
    SPHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocodingGateway = new GeocodingGateway(this, this);
        periodicGateway = new PeriodicGateway(this, this);
        gateway = new ServerGateway(this, this);
        spHelper = SPHelper.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        periodicGateway.pauseGetBusses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        periodicGateway.restartGetBusses();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        periodicGateway.destroy();
    }


    public <T extends Fragment> T findFragment(String tag, Class<T> type) throws IllegalAccessException, InstantiationException {
        FragmentManager fm = getSupportFragmentManager();
        T mFragment = (T)fm.findFragmentByTag(tag);
        if(mFragment==null){
            T fragment = type.newInstance();
            return fragment;
        }else{
            return mFragment;
        }
    }

    public void setFragment(Fragment fragment, int where){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(where, fragment, null);
        ft.commit();
    }


    @Override
    public void onSucces(AbstractPOJO response) {

    }

    @Override
    public void onFailure(String message) {

    }
}
