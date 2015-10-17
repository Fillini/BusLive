package fill.com.buslive.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import fill.com.buslive.R;
import fill.com.buslive.event.CheckRouteEvent;
import fill.com.buslive.fragments.views.RoutesComponent;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;

/**
 * Created by Fill on 07.10.2015.
 */
public class RoutesFragment extends Fragment {


    public static final String TAG = "routesFragment";

    RoutesComponent routes_component;
    EventBus eventBus = EventBus.getDefault();

    static final String ROUTES_PARAM = "routes_param";
    static final String CHECKED_ROUTES_PARAM = "checked_routes_param";


    Routes routes;
    Routes checkedRoutes;

    public RoutesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(routes_component==null){
            routes_component = (RoutesComponent)LayoutInflater.from(getContext()).inflate(R.layout.fragment_routes_list, container, false);
        }

        if(savedInstanceState!=null){
            restoreFromSavedState(savedInstanceState);
        }

        if(routes!=null){
            routes_component.setRoutes(routes);
        }
        if(checkedRoutes!=null){
            routes_component.setCheckedset(checkedRoutes);
        }

        routes_component.setOnCheckRouteListener(new RoutesComponent.OnCheckRouteListener() {
            @Override
            public void onCheckRoute(Routes checkedRoutes) {
                CheckRouteEvent event = new CheckRouteEvent();
                event.setChecked_route(checkedRoutes);
                eventBus.post(event);
            }
        });

        return routes_component;
    }

    private void restoreFromSavedState(Bundle savedInstanceState) {
        routes = (Routes) savedInstanceState.getSerializable(ROUTES_PARAM);
        checkedRoutes = (Routes) savedInstanceState.getSerializable(CHECKED_ROUTES_PARAM);
    }

    public void setRoutes(Routes routes, Routes checkedRoutes){
        this.routes = routes;
        this.checkedRoutes = checkedRoutes;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ROUTES_PARAM, routes);
        outState.putSerializable(CHECKED_ROUTES_PARAM, checkedRoutes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        routes_component.setOnCheckRouteListener(null);
        eventBus.unregister(this);
    }
}
