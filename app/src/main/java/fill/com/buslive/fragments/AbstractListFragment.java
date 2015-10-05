package fill.com.buslive.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fill.com.buslive.http.ResponseCallback;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Cities;
import fill.com.buslive.http.pojo.Countries;

/**
 * Created by Fill on 15.12.2014.
 */
public abstract class AbstractListFragment extends ListFragment implements ResponseCallback {


    public ServerGateway gateway;


    protected AbstractListFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gateway = new ServerGateway(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void onSucces(AbstractPOJO response) {
        if(response instanceof Countries){
            onCountries((Countries) response);
        }
        if(response instanceof Cities){
            onCities((Cities)response);
        }
    }

    @Override
    public void onFailure(String message) {

    }


    public void onCountries(Countries countries){

    }

    public void onCities(Cities cities){

    }

}
