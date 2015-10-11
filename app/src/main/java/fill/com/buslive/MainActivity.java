package fill.com.buslive;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import fill.com.buslive.event.CheckRouteEvent;
import fill.com.buslive.event.ClickStationEvent;
import fill.com.buslive.fragments.RoutesFragment;
import fill.com.buslive.fragments.views.RoutesComponent;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Geocode;
import fill.com.buslive.http.pojo.RouteStations;
import fill.com.buslive.http.pojo.Routes;

import fill.com.buslive.http.pojo.Stations;
import fill.com.buslive.utils.MapDrawHelper;
import material.MaterialProgressBar;


//TODO: Сделать напоминалку (типа будильник, заводишь на определенное время, на определенный автобус)


//TODO: Сделать сплэш экран

public class MainActivity extends GatewaedActivity {


    SupportMapFragment mapFragment;
    GoogleMap map;
    MapDrawHelper mapDrawHelper;
    SlidingUpPanelLayout sliding_layout;
    LinearLayout slideView;
    ImageButton bus_btn;
    Toolbar toolbar;
    ImageView chevron_iv;
    ImageView ic_back;
    TextView sliding_title_tv;

    RoutesComponent route_view;

    MaterialProgressBar progress_bar;

    ArrayList<Routes.Route> checkedRoute;
    Routes routes;
    Stations stations;
    RouteStations routeStations;

    EventBus eventbus = EventBus.getDefault();

    LinearLayout slide_container;


    public static final int SETTINGS_RESULT = 1;
    public static final int ROUTES_RESULT = 2;
    public static final int STATIONS_RESULT = 3;

    static final String CHECKED_ROUTES_KEY = "checked_routes"; /* отмеченные маршруты*/
    static final String ROUTES_KEY = "routes"; /* все маршруты*/
    static final String STATIONS_KEY = "stations"; /* все остановки*/
    static final String ROUTESTATIONS_KEY = "routestations"; /* все остановки*/

    static final String CURRENT_ZOOM_MAP_KEY = "current_zoom_key";
    static final String CURRENT_LAT_LNG_KEY = "current_lat_lng_key";
    static final String SLIDING_STATE_KEY = "sliding_state_key";

    private float current_zoom = 12;
    private LatLng current_latlng = new LatLng(51.154191, 71.416905); // astana coords
    private String current_sliding_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventbus.register(this);
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slideView = (LinearLayout) findViewById(R.id.slideView);
        bus_btn = (ImageButton) findViewById(R.id.bus_btn);
        progress_bar = (MaterialProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setCircleBackgroundEnabled(false);
        route_view = (RoutesComponent) findViewById(R.id.route_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        chevron_iv = (ImageView) findViewById(R.id.chevron_iv);
        ic_back = (ImageView) findViewById(R.id.ic_back);
        sliding_title_tv = (TextView) findViewById(R.id.route_tv);
        slide_container = (LinearLayout)findViewById(R.id.slide_container);

        setSupportActionBar(toolbar);

        setTitle(R.string.busses);

        adjustMap();
        setListeners();

        progress_bar.setColorSchemeColors(getResources().getIntArray(R.array.routecolors));

        sliding_layout.setAnchorPoint(0.5f);



        progress_bar.setVisibility(View.GONE);
        ic_back.setAlpha(0.0f);
        sliding_title_tv.setAlpha(0.0f);






        if (!spHelper.isSettingsExists()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_RESULT);
        }

        if (savedInstanceState != null) {
            restoreFromSavedInstance(savedInstanceState);
        }


        restoreSlidingPanelState();

        if (checkedRoute != null) {
            periodicGateway.startGetBusses(checkedRoute);
        }
        if (routes == null) {
            progress_bar.setVisibility(View.VISIBLE);
            gateway.getRoutes(spHelper.getCity());
        }
        if(routeStations==null){
            gateway.getRouteStations(spHelper.getCity());
        }


    }


    public void onEvent(CheckRouteEvent event){
        setCheckedRoute(event.getChecked_route());
    }

    public void onEvent(ClickStationEvent event){

        Stations.Station station = event.getStation();
        Routes routes_on_station = new Routes();

        ArrayList<String> route_ids = new ArrayList<>();

        String station_id = station.getId();


        for(RouteStations.RouteStation routestation: routeStations.getRouteStations()){

            if(routestation.getStationId().equals(station_id)){
                String route_id = routestation.getRouteId();
                route_ids.add(route_id);
            }
        }


        for(Routes.Route route: routes.getRoutes()){

            for(String route_id: route_ids){
                if(route_id.equals(route.getBusreportRouteId())){
                    routes_on_station.addRoute(route);
                }
            }
        }

        setRoutesOnRouteFragment(routes_on_station, new ArrayList<Routes.Route>());

        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        sliding_title_tv.setText(station.getName());


    }






    private void adjustMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomGesturesEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(true);

                solveLocation();


                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                    map.setPadding(0, actionBarHeight, 0, 0);
                }

                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setMyLocationEnabled(true);
                View zoomControlView = (
                        (View) mapFragment.getView().findViewById(Integer.parseInt("1"))
                                .getParent())
                        .findViewById(Integer.parseInt("1"));
                if (zoomControlView != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.setMargins(10, 0, 0, 10);

                    zoomControlView.setLayoutParams(params);
                }


                View myLocationControl = (
                        (View) mapFragment.getView().findViewById(Integer.parseInt("1"))
                                .getParent())
                        .findViewById(Integer.parseInt("2"));
                if (myLocationControl != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.setMargins(0, 0, 10, 10);
                    myLocationControl.setLayoutParams(params);
                }
                map.moveCamera(CameraUpdateFactory.zoomTo(current_zoom));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_latlng, current_zoom));
                mapDrawHelper = new MapDrawHelper(map, MainActivity.this);
                if (checkedRoute != null) {
                    mapDrawHelper.drawRoutes(checkedRoute);
                }
            }
        });
    }

    /**
     * Вызвывается когда были изменены настройки
     */
    public void solveLocation() {
        if (spHelper.getCoords().equals("")) {
            geocodingGateway.getGeocodeInfo(spHelper.getCountry_name(), spHelper.getCity_name());
        } else {
            String coords = spHelper.getCoords();
            String[] latlng = coords.split(";");
            current_latlng = new LatLng(Double.valueOf(latlng[0]), Double.valueOf(latlng[1]));
            if(stations!=null){
                mapDrawHelper.drawStations(stations);
            }else{
                gateway.getStations(spHelper.getCity());
            }
        }
    }



    private void setListeners() {
        bus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (routes == null) {
                    gateway.getRoutes(spHelper.getCity());
                    progress_bar.setVisibility(View.VISIBLE);
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    setRoutesOnRouteFragment(routes, checkedRoute);
                    sliding_title_tv.setText("Маршруты");
                }

            }
        });

        sliding_layout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                float toolbar_offset = toolbar.getHeight() * slideOffset;
                if (toolbar_offset < 0) {
                    toolbar_offset = 0.0f;
                }
                toolbar.setTranslationY(-1 * toolbar_offset * 2);


                if (slideOffset <= sliding_layout.getAnchorPoint()) {
                    chevron_iv.setRotation(slideOffset * -180*2);
                    chevron_iv.setAlpha(1.0f);
                    ic_back.setAlpha(0.0f);
                    sliding_title_tv.setAlpha(0.0f);
                }

                if (slideOffset > sliding_layout.getAnchorPoint()) {
                    chevron_iv.setAlpha((1.0f - slideOffset) * 2);

                    float alpha_in = (-0.5f + slideOffset) * 2;

                    ic_back.setAlpha(alpha_in);
                    sliding_title_tv.setAlpha(alpha_in);
                    sliding_title_tv.setTranslationX(alpha_in * -30);

                }

            }

            @Override
            public void onPanelCollapsed(View panel) {

            }

            @Override
            public void onPanelExpanded(View panel) {


            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }


    private void restoreSlidingPanelState(){

        if(current_sliding_state==null){
            progress_bar.setVisibility(View.GONE);
            ic_back.setAlpha(0.0f);
            sliding_title_tv.setAlpha(0.0f);
            return;
        }
        if(current_sliding_state.equals(SlidingUpPanelLayout.PanelState.COLLAPSED.toString())){

            chevron_iv.setRotation(0);
            chevron_iv.setAlpha(1.0f);
            ic_back.setAlpha(0.0f);
            sliding_title_tv.setAlpha(0.0f);

            return;
        }
        if(current_sliding_state.equals(SlidingUpPanelLayout.PanelState.ANCHORED.toString())){
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                toolbar.setTranslationY(-1 * actionBarHeight * 2);
            }
            chevron_iv.setRotation(180);
            chevron_iv.setAlpha(1.0f);
            ic_back.setAlpha(0.0f);
            sliding_title_tv.setAlpha(0.0f);

            return;
        }
        if(current_sliding_state.equals(SlidingUpPanelLayout.PanelState.EXPANDED.toString())){
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                toolbar.setTranslationY(-1 * actionBarHeight * 2);
            }
            chevron_iv.setRotation(180);
            chevron_iv.setAlpha(0.0f);
            ic_back.setAlpha(1.0f);
            sliding_title_tv.setAlpha(1.0f);
            sliding_title_tv.setTranslationX(-1.0f * 40);

            return;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTINGS_RESULT:
                spHelper.setCoords("");
                solveLocation();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CHECKED_ROUTES_KEY, checkedRoute);
        outState.putSerializable(ROUTES_KEY, routes);
        outState.putSerializable(STATIONS_KEY, stations);
        outState.putSerializable(ROUTESTATIONS_KEY, routeStations);
        outState.putString(SLIDING_STATE_KEY, sliding_layout.getPanelState().toString());
        if(map!=null){
            outState.putFloat(CURRENT_ZOOM_MAP_KEY, map.getCameraPosition().zoom);
            outState.putParcelable(CURRENT_LAT_LNG_KEY, map.getCameraPosition().target);
        }
    }

    /**
     * Сохраняем выбранные маршруты, актуальный зум, актуальные координаты
     *
     * @param savedInstanceState
     */
    private void restoreFromSavedInstance(Bundle savedInstanceState) {
        checkedRoute = ((ArrayList) savedInstanceState.getSerializable(CHECKED_ROUTES_KEY));
        routes = (Routes) savedInstanceState.getSerializable(ROUTES_KEY);
        stations = (Stations) savedInstanceState.getSerializable(STATIONS_KEY);
        routeStations = (RouteStations)savedInstanceState.getSerializable(ROUTESTATIONS_KEY);
        current_sliding_state = savedInstanceState.getString(SLIDING_STATE_KEY);
        if(savedInstanceState.getParcelable(CURRENT_LAT_LNG_KEY)!=null){
            current_latlng = savedInstanceState.getParcelable(CURRENT_LAT_LNG_KEY);
            current_zoom = savedInstanceState.getFloat(CURRENT_ZOOM_MAP_KEY);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bus_live, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_RESULT);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод получает выбраные маршруты и запускает  периодическое обновление координат автобусов
     *
     * @param checkedRoute - выбраные маршруты
     */
    public void setCheckedRoute(ArrayList<Routes.Route> checkedRoute) {
        this.checkedRoute = checkedRoute;
        if (this.checkedRoute != null && this.checkedRoute.size()>0) {
            periodicGateway.startGetBusses(this.checkedRoute);
            mapDrawHelper.drawRoutes(this.checkedRoute);
        }
        if(this.checkedRoute.size()==0){
            mapDrawHelper.drawRoutes(this.checkedRoute);
        }
    }

    @Override
    public void onBackPressed() {
        if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSucces(AbstractPOJO response) {
        if (response instanceof Geocode) {
            Geocode geocode = (Geocode) response;
            spHelper.setCoords(geocode.getLatitude() + ";" + geocode.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geocode.getLatitude(), geocode.getLongitude()), current_zoom));
            /*обновляем данные об остановках*/
            gateway.getStations(spHelper.getCity());
        }
        if (response instanceof Busses) {
            Busses busses = (Busses) response;
            mapDrawHelper.drawBusses(busses);
        }
        if(response instanceof Stations){
            Stations stations = (Stations)response;
            mapDrawHelper.drawStations(stations);
        }

        if(response instanceof RouteStations){
            RouteStations routeStations = (RouteStations)response;
            this.routeStations = routeStations;
        }

        if (response instanceof Routes) {
            this.routes = (Routes) response;
            checkedRoute = new ArrayList<>();
            setCheckedRoute(checkedRoute);

            setRoutesOnRouteFragment(this.routes, checkedRoute);

            progress_bar.setVisibility(View.GONE);
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            sliding_title_tv.setText("Маршруты");

            /* для плавности запускаем немного позже, после того как фрагмент добавится*/
           /* Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress_bar.setVisibility(View.GONE);
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }, 500);*/

        }

    }


    /**
     *
     * @param routes маршруты которые в списке
     * @param checkedRoute отмеченые маршруты (если надо)
     */
    private void setRoutesOnRouteFragment(Routes routes, ArrayList<Routes.Route> checkedRoute){
        RoutesFragment fragment = findRoutesFragment();
        if(fragment!=null){
            fragment.setRoutes(routes, checkedRoute);
        }else{
            throw new RuntimeException("RoutesFragment is null");
        }

    }



    private RoutesFragment findRoutesFragment(){

        FragmentManager fm = getSupportFragmentManager();
        RoutesFragment mFragment = (RoutesFragment)fm.findFragmentByTag(RoutesFragment.TAG);
        if(mFragment==null){
            FragmentTransaction ft = fm.beginTransaction();
            RoutesFragment fragment = RoutesFragment.newInstance(this.routes, checkedRoute);
            ft.replace(R.id.slide_container, fragment, null);
            try {
                ft.commit();
                return fragment;
            }catch (IllegalStateException e){
                return null;
            }
        }else{
            if(mFragment.isInLayout()){
                return mFragment;
            }
        }

        return null;

    }


    @Override
    public void onFailure(String message) {
        Snackbar.make(sliding_layout, "Ошибка получения данных.", Snackbar.LENGTH_SHORT).show();
        progress_bar.setVisibility(View.GONE);
    }
    /*--------------------------------*/


    @Override
    protected void onResume() {
        super.onResume();

        if (routes == null) {
            gateway.getRoutes(spHelper.getCity());
            progress_bar.setVisibility(View.VISIBLE);
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventbus.unregister(this);
    }
}
