package fill.com.buslive;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import fill.com.buslive.event.CheckRouteEvent;
import fill.com.buslive.event.ClickStationEvent;
import fill.com.buslive.fragments.RoutesFragment;
import fill.com.buslive.fragments.TimeTableFragment;
import fill.com.buslive.fragments.views.RoutesComponent;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Geocode;
import fill.com.buslive.http.pojo.RouteStations;
import fill.com.buslive.http.pojo.Routes;

import fill.com.buslive.http.pojo.RoutesOnStations;
import fill.com.buslive.http.pojo.Stations;
import fill.com.buslive.utils.L;
import fill.com.buslive.utils.LocationUtils;
import fill.com.buslive.utils.MapDrawHelper;

import material.MaterialProgressBar;


//TODO: Сделать напоминалку (типа будильник, заводишь на определенное время, на определенный автобус) (невозможно)

//TODO: Сделать сплэш экран с индикацией загрузки контента.

//TODO: Реализовать паттерн state

//TODO: Сделать закладки остановок и маршрутов (типа любимый маршрут или остановка для быстрого поиска)
// TODO: Сделать выделенную остановку


public class MainActivity extends GatewaedActivity implements GoogleMap.OnMyLocationChangeListener {


    SupportMapFragment mapFragment;
    GoogleMap map;
    MapDrawHelper mapDrawHelper;
    SlidingUpPanelLayout sliding_layout;
    LinearLayout slideView;
    ImageButton bus_btn;
    ImageButton my_station_btn;
    Toolbar toolbar;
    ImageView chevron_iv;
    ImageView ic_back;
    TextView sliding_title_tv;

    RoutesComponent route_view;

    MaterialProgressBar progress_bar;

    Routes checkedRoute;
    Routes routes;
    Stations stations;
    RouteStations routeStations;

    EventBus eventbus = EventBus.getDefault();

    LinearLayout slide_container;


    public static final int SETTINGS_RESULT = 1;
    public static final int ROUTES_RESULT = 2;

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
    private String currentStation_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventbus.register(this);
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slideView = (LinearLayout) findViewById(R.id.slideView);
        bus_btn = (ImageButton) findViewById(R.id.bus_btn);
        my_station_btn = (ImageButton) findViewById(R.id.my_station_btn);
        progress_bar = (MaterialProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setCircleBackgroundEnabled(false);
        route_view = (RoutesComponent) findViewById(R.id.route_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        chevron_iv = (ImageView) findViewById(R.id.chevron_iv);
        ic_back = (ImageView) findViewById(R.id.ic_back);
        sliding_title_tv = (TextView) findViewById(R.id.route_tv);
        slide_container = (LinearLayout) findViewById(R.id.slide_container);

        setSupportActionBar(toolbar);

        setTitle(R.string.busses);

        adjustMap();
        setListeners();

        progress_bar.setColorSchemeColors(getResources().getIntArray(R.array.routecolors));

        sliding_layout.setAnchorPoint(0.5f);


        progress_bar.setVisibility(View.GONE);
        my_station_btn.setVisibility(View.GONE);

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
        if (routeStations == null) {
            gateway.getRouteStations(spHelper.getCity());
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


    public void onEvent(CheckRouteEvent event) {
        setCheckedRoute(event.getChecked_route());
    }


    public void onEvent(ClickStationEvent event) {
        Stations.Station station = event.getStation();
        gateway.getRoutesOnStations(spHelper.getCity(), station.getId());
        progress_bar.setVisibility(View.VISIBLE);
        sliding_title_tv.setText("Остановка: " + station.getName());
        currentStation_id = station.getId();
    }


    private void adjustMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                mapDrawHelper = new MapDrawHelper(map, MainActivity.this);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomGesturesEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(true);

                map.setOnMyLocationChangeListener(MainActivity.this);

                solveLocation();

                map.setPadding(0, getActionBarHeight(), 0, 0);

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
            if (stations != null) {
                mapDrawHelper.drawStations(stations);
            } else {
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
                    chevron_iv.setRotation(slideOffset * -180 * 2);
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


    private void restoreSlidingPanelState() {

        if (current_sliding_state == null) {
            progress_bar.setVisibility(View.GONE);
            ic_back.setAlpha(0.0f);
            sliding_title_tv.setAlpha(0.0f);
            return;
        }
        if (current_sliding_state.equals(SlidingUpPanelLayout.PanelState.COLLAPSED.toString())) {

            chevron_iv.setRotation(0);
            chevron_iv.setAlpha(1.0f);
            ic_back.setAlpha(0.0f);
            sliding_title_tv.setAlpha(0.0f);
            return;
        }
        if (current_sliding_state.equals(SlidingUpPanelLayout.PanelState.ANCHORED.toString())) {
            toolbar.setTranslationY(-1 * getActionBarHeight() * 2);
            chevron_iv.setRotation(180);
            chevron_iv.setAlpha(1.0f);
            ic_back.setAlpha(0.0f);
            sliding_title_tv.setAlpha(0.0f);

            return;
        }
        if (current_sliding_state.equals(SlidingUpPanelLayout.PanelState.EXPANDED.toString())) {
            toolbar.setTranslationY(-1 * getActionBarHeight() * 2);
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
        if (map != null) {
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
        checkedRoute = (Routes) savedInstanceState.getSerializable(CHECKED_ROUTES_KEY);
        routes = (Routes) savedInstanceState.getSerializable(ROUTES_KEY);
        stations = (Stations) savedInstanceState.getSerializable(STATIONS_KEY);
        routeStations = (RouteStations) savedInstanceState.getSerializable(ROUTESTATIONS_KEY);
        current_sliding_state = savedInstanceState.getString(SLIDING_STATE_KEY);
        if (savedInstanceState.getParcelable(CURRENT_LAT_LNG_KEY) != null) {
            current_latlng = savedInstanceState.getParcelable(CURRENT_LAT_LNG_KEY);
            current_zoom = savedInstanceState.getFloat(CURRENT_ZOOM_MAP_KEY);
        }

    }



    /**
     * Метод получает выбраные маршруты и запускает  периодическое обновление координат автобусов
     *
     * @param checkedRoute - выбраные маршруты
     */
    public void setCheckedRoute(Routes checkedRoute) {
        this.checkedRoute = checkedRoute;
        if (this.checkedRoute != null && this.checkedRoute.size() > 0 && mapDrawHelper!=null) {
            periodicGateway.startGetBusses(this.checkedRoute);
            mapDrawHelper.drawRoutes(this.checkedRoute);
        }
        if (this.checkedRoute.size() == 0 && mapDrawHelper!=null) {
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
            routes = null;
            gateway.getStations(spHelper.getCity());
        }
        if (response instanceof Busses) {
            Busses busses = (Busses) response;
            mapDrawHelper.drawBusses(busses);
        }
        if (response instanceof Stations) {
            Stations stations = (Stations) response;
            this.stations = stations;
            mapDrawHelper.drawStations(stations);
        }
        if (response instanceof RoutesOnStations) {
            progress_bar.setVisibility(View.GONE);
            RoutesOnStations routes_ids = (RoutesOnStations) response;
            Routes routes_on_station = new Routes();

            for (Routes.Route route : routes.getRoutes()) {
                for (Integer route_id : routes_ids.getRoutes_on_stations()) {
                    String s = route_id + "";
                    if (s.equals(route.getBusreportRouteId())) {
                        routes_on_station.addRoute(route);
                    }
                }
            }

            setTimeTableFragment(routes_on_station);
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }

        if (response instanceof RouteStations) {
            RouteStations routeStations = (RouteStations) response;
            this.routeStations = routeStations;
        }

        if (response instanceof Routes) {
            this.routes = (Routes) response;
            checkedRoute = new Routes();
            setCheckedRoute(checkedRoute);

            setRoutesOnRouteFragment(this.routes, checkedRoute);

            progress_bar.setVisibility(View.GONE);
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            sliding_title_tv.setText("Маршруты");
        }

    }


    /**
     * @param routes       маршруты которые в списке
     * @param checkedRoute отмеченые маршруты (если надо)
     */
    private void setRoutesOnRouteFragment(Routes routes, Routes checkedRoute) {
        RoutesFragment fragment = findRoutesFragment();
        setFragment(fragment, R.id.slide_container);
        if (fragment != null) {
            fragment.setRoutes(routes, checkedRoute);
        } else {
            throw new RuntimeException("RoutesFragment is null");
        }
    }

    private void setTimeTableFragment(Routes routes_on_station) {
        TimeTableFragment fragment = findTimeTableFragment();
        setFragment(fragment, R.id.slide_container);
        if (fragment != null) {
            fragment.set_routes_on_station(routes_on_station, checkedRoute, currentStation_id);
        } else {
            throw new RuntimeException("TimeTableFragment is null");
        }
    }

    private RoutesFragment findRoutesFragment() {
        try {
            return findFragment(RoutesFragment.TAG, RoutesFragment.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private TimeTableFragment findTimeTableFragment() {
        try {
            return findFragment(TimeTableFragment.TAG, TimeTableFragment.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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

    @Override
    public void onMyLocationChange(Location location) {
        if (stations == null) {
            return;
        }
        final Stations.Station closer_station = findCloserStation(location);
        if(closer_station!=null){
            if(my_station_btn.getVisibility()!=View.VISIBLE){
                playNotifySound();
            }
            my_station_btn.setVisibility(View.VISIBLE);

            my_station_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentStation_id = closer_station.getId();
                    gateway.getRoutesOnStations(spHelper.getCity(), currentStation_id);
                    progress_bar.setVisibility(View.VISIBLE);
                    sliding_title_tv.setText("Остановка: " + closer_station.getName());
                }
            });

        }else{
            my_station_btn.setVisibility(View.GONE);
        }

    }

    private Stations.Station findCloserStation(Location location){
        for (Stations.Station station : stations.getStations()) {
            LatLng station_location = station.getLatlon().toLatLng();
            LatLng my_location = new LatLng(location.getLatitude(), location.getLongitude());
            double distance = LocationUtils.distance(station_location, my_location);
            if (distance < 20) {
                return station;
            }
        }
        return null;
    }


}
