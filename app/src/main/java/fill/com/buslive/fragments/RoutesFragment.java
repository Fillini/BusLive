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
    ArrayList<Routes.Route> checkedRoutes;


    public static RoutesFragment newInstance(Routes routes, ArrayList<Routes.Route> checkedRoutes){
        RoutesFragment fragment = new RoutesFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ROUTES_PARAM, routes);
        arguments.putSerializable(CHECKED_ROUTES_PARAM, checkedRoutes);
        fragment.setArguments(arguments);
        return fragment;
    }

    public RoutesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        eventBus.register(this);

        if(routes_component==null){
            routes_component = (RoutesComponent)LayoutInflater.from(getContext()).inflate(R.layout.fragment_routes_list, container, false);
        }

        if(routes==null){
            routes = ((Routes) getArguments().getSerializable(ROUTES_PARAM));
        }
        if(checkedRoutes==null){
            checkedRoutes = ((ArrayList) getArguments().getSerializable(CHECKED_ROUTES_PARAM));
        }

        routes_component.setRoutes(routes);
        routes_component.setCheckedset(checkedRoutes);

        routes_component.setOnCheckRouteListener(new RoutesComponent.OnCheckRouteListener() {
            @Override
            public void onCheckRoute(ArrayList<Routes.Route> checkedRoutes) {
                CheckRouteEvent event = new CheckRouteEvent();
                event.setChecked_route(checkedRoutes);
                eventBus.post(event);
            }
        });

        return routes_component;
    }

    public void setRoutes(Routes routes, ArrayList<Routes.Route> checkedRoutes){

        this.routes = routes;
        this.checkedRoutes = checkedRoutes;



    }



    public void onEvent(Object event){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        routes_component.setOnCheckRouteListener(null);
        eventBus.unregister(this);
    }
}
