package fill.com.buslive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fill.com.buslive.R;
import fill.com.buslive.fragments.views.TimeTableComponent;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;

/**
 * Created by Fill on 14.10.2015.
 */
public class TimeTableFragment extends Fragment {

    public static final String TAG = "timeTableFragment";

    TimeTableComponent time_table_component;

    public static final String ROUTES_ON_STATION_PARAM = "routes_on_station_param";
    public static final String CHECKED_ROUTES_PARAM = "checked_routes_param";
    public static final String STATION_ID_PARAM = "current_station_param";


    Routes routes_on_station;
    Routes checked_routes;
    String currentStation_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(time_table_component==null){
            time_table_component = ((TimeTableComponent) LayoutInflater.from(getContext()).inflate(R.layout.fragment_time_table, container, false));
        }

        if(savedInstanceState!=null){
            restoreFromSavedState(savedInstanceState);
        }

        if(routes_on_station!=null){
            time_table_component.setRoutes_on_station(routes_on_station, checked_routes, currentStation_id);
        }

        return time_table_component;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ROUTES_ON_STATION_PARAM, routes_on_station);
        outState.putSerializable(CHECKED_ROUTES_PARAM, checked_routes);
        outState.putString(STATION_ID_PARAM, currentStation_id);
    }

    private void restoreFromSavedState(Bundle savedInstanceState) {
        routes_on_station = (Routes) savedInstanceState.getSerializable(ROUTES_ON_STATION_PARAM);
        checked_routes = (Routes) savedInstanceState.getSerializable(CHECKED_ROUTES_PARAM);
        currentStation_id = savedInstanceState.getString(STATION_ID_PARAM);
    }


    public void set_routes_on_station(Routes routes_on_station, Routes checked_routes, String currentStation_id){
        this.routes_on_station = routes_on_station;
        this.checked_routes = checked_routes;
        this.currentStation_id = currentStation_id;
    }

}
