package fill.com.buslive.fragments;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fill.com.buslive.http.ResponseCallback;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.utils.SPHelper;


public abstract class AbstractFragment extends Fragment implements ResponseCallback {

    public ServerGateway gateway;
    public SPHelper spHelper;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gateway = new ServerGateway(getActivity().getApplicationContext(), this);
        spHelper = SPHelper.getInstance(getActivity().getApplicationContext());
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

    }

    @Override
    public void onFailure(String message) {
        if(getActivity()!=null){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
