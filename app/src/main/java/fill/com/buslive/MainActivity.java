package fill.com.buslive;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import fill.com.buslive.fragments.views.RoutesComponent;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Geocode;
import fill.com.buslive.http.pojo.Routes;

import fill.com.buslive.utils.L;
import fill.com.buslive.utils.MapDrawHelper;
import material.MaterialProgressBar;


//TODO: Сделать напоминалку (типа будильник, заводишь на определенное время, на определенный автобус)

//TODO: Если SlidingPanel expanded и перевернуть телефон, то toolbar перекрывает его (Исправить)
public class MainActivity extends GatewaedActivity {


    SupportMapFragment mapFragment;
    GoogleMap map;
    MapDrawHelper mapDrawHelper;
    SlidingUpPanelLayout sliding_layout;
    LinearLayout slideView;
    ImageButton bus_btn;
    Toolbar toolbar;
    ImageView chevron_iv;

    RoutesComponent route_view;

    MaterialProgressBar progress_bar;

    ArrayList<Routes.Route> checkedRoute;
    Routes routes;


    public static final int SETTINGS_RESULT = 1;
    public static final int ROUTES_RESULT = 2;
    public static final int STATIONS_RESULT = 3;

    static final String CHECKED_ROUTES_KEY = "checked_routes"; /* отмеченные маршруты*/
    static final String ROUTES_KEY = "routes"; /* все маршруты*/
    static final String CURRENT_ZOOM_MAP_KEY = "current_zoom_key";
    static final String CURRENT_LAT_LNG_KEY = "current_lat_lng_key";
    private float current_zoom = 12;
    private LatLng current_latlng = new LatLng(51.154191, 71.416905); // astana coords


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slideView = (LinearLayout) findViewById(R.id.slideView);
        bus_btn = (ImageButton) findViewById(R.id.bus_btn);
        progress_bar = (MaterialProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setCircleBackgroundEnabled(false);
        route_view = (RoutesComponent) findViewById(R.id.route_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        chevron_iv = (ImageView) findViewById(R.id.chevron_iv);

        setSupportActionBar(toolbar);

        setTitle(R.string.busses);

        adjustMap();
        setListeners();



        progress_bar.setColorSchemeColors(getResources().getIntArray(R.array.routecolors));
        progress_bar.setVisibility(View.GONE);

        sliding_layout.setAnchorPoint(0.5f);

        if (!spHelper.isSettingsExists()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_RESULT);
        }

        if (savedInstanceState != null) {
            restoreFromSavedInstance(savedInstanceState);
        }


        if (checkedRoute != null) {
            route_view.setCheckedset(checkedRoute);
            periodicGateway.startGetBusses(checkedRoute);
        }
        if (routes != null) {
            route_view.setRoutes(routes);
        }



/*
        Intent intent = new Intent(this, RoutesActivity.class);
        startActivityForResult(intent, ROUTES_RESULT);
*/
    }

    /**
     * Сохраняем выбранные маршруты, актуальный зум, актуальные координаты
     *
     * @param savedInstanceState
     */
    private void restoreFromSavedInstance(Bundle savedInstanceState) {
        checkedRoute = ((ArrayList) savedInstanceState.getSerializable(CHECKED_ROUTES_KEY));
        routes = (Routes) savedInstanceState.getSerializable(ROUTES_KEY);

        if(savedInstanceState.getParcelable(CURRENT_LAT_LNG_KEY)!=null){
            current_latlng = savedInstanceState.getParcelable(CURRENT_LAT_LNG_KEY);
            current_zoom = savedInstanceState.getFloat(CURRENT_ZOOM_MAP_KEY);
        }

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
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
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
        }
    }

    private void setListeners() {
        bus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gateway.getRoutes(spHelper.getCity());
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                progress_bar.setVisibility(View.VISIBLE);
            }
        });

        route_view.setOnCheckRouteListener(new RoutesComponent.OnCheckRouteListener() {
            @Override
            public void onCheckRoute(ArrayList<Routes.Route> checkedRoutes) {
                setCheckedRoute(checkedRoutes);
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
            }

            @Override
            public void onPanelCollapsed(View panel) {
                chevron_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_double_up));
            }

            @Override
            public void onPanelExpanded(View panel) {
                chevron_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_double_down));
            }

            @Override
            public void onPanelAnchored(View panel) {
                chevron_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_double_down));
            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
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
        if(map!=null){
            outState.putFloat(CURRENT_ZOOM_MAP_KEY, map.getCameraPosition().zoom);
            outState.putParcelable(CURRENT_LAT_LNG_KEY, map.getCameraPosition().target);
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
        if (this.checkedRoute != null) {
            periodicGateway.startGetBusses(checkedRoute);
            mapDrawHelper.drawRoutes(checkedRoute);
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
            bus_btn.performClick();
        }
        if (response instanceof Busses) {
            Busses busses = (Busses) response;
            mapDrawHelper.drawBusses(busses);
        }
        if (response instanceof Routes) {
            progress_bar.setVisibility(View.GONE);
            this.routes = (Routes) response;
            route_view.setRoutes(routes);
            checkedRoute = new ArrayList<>();
            route_view.setCheckedset(checkedRoute);
            setCheckedRoute(checkedRoute);
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }

    }

    @Override
    public void onFailure(String message) {
        Snackbar.make(sliding_layout, "Ошибка получения данных.", Snackbar.LENGTH_SHORT).show();
        progress_bar.setVisibility(View.GONE);
    }
    /*--------------------------------*/


}
