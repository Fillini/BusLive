package fill.com.buslive;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import fill.com.buslive.fragments.views.RoutesComponent;
import fill.com.buslive.http.ResponseCallback;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;
import fill.com.buslive.utils.SPHelper;


public class RoutesActivity extends AppCompatActivity implements ResponseCallback, View.OnClickListener{

    Routes routes;
    RoutesComponent routesComponent;
    //SwipeRefreshLayout swipe_refresh_layout;


    FloatingActionButton fab;
    ServerGateway gateway;
    SPHelper spHelper;


    static String ROUTES_KEY = "routes key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);


        gateway = new ServerGateway(this, this);
        spHelper = SPHelper.getInstance(this);

        routesComponent = (RoutesComponent) findViewById(R.id.route_view);
        //swipe_refresh_layout = (SwipeRefreshLayout) routesComponent.findViewById(R.id.swipe_refresh_layout);

        //swipe_refresh_layout.setColorSchemeColors(getResources().getIntArray(R.array.routecolors));


        fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(this);

        if(savedInstanceState!=null){
            restoreFromSavedInstance(savedInstanceState);
        }else{
            gateway.getRoutes(spHelper.getCity());
            setSwipeRefreshing(true);
        }

        /*swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gateway.getRoutes(spHelper.getCity());
            }
        });*/

    }

    private void restoreFromSavedInstance(Bundle savedInstanceState){
        routes = ((Routes) savedInstanceState.getSerializable(ROUTES_KEY));
        if(routes!=null){
            routesComponent.setRoutes(routes);
        }else{
            gateway.getRoutes(spHelper.getCity());
            setSwipeRefreshing(true);
        }
    }


    private void setSwipeRefreshing(final Boolean isRefreshing){
        /*swipe_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_layout.setRefreshing(isRefreshing);
            }
        });*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ROUTES_KEY, routes);
    }

    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        data.putExtra("checkedRoutes", routesComponent.getCheckedSet());
        setResult(MainActivity.ROUTES_RESULT, data);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("checkedRoutes", routesComponent.getCheckedSet());
        setResult(MainActivity.ROUTES_RESULT, data);
        super.onBackPressed();
    }

    @Override
    public void onSucces(AbstractPOJO response) {
        if(response instanceof Routes){
            routes  = ((Routes) response);
            routesComponent.setRoutes(routes);
            setSwipeRefreshing(false);
        }
    }

    @Override
    public void onFailure(String message) {
        L.trace(message);
        setSwipeRefreshing(false);
    }
}
