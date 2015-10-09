package fill.com.buslive.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import fill.com.buslive.R;
import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.http.pojo.Stations;

/**
 * Компонетн рисует на карте маркеры и линии.
 * Created by Fill on 15.01.2015.
 */
public class MapDrawHelper {




    enum CIRCLE_MODE{
        CIRCLE,ARROW_CIRCLE
    }


    private static final float PREMEDIUM_ZOOM_THRESHOLD = 14;
    private static final float MEDIUM_ZOOM_THRESHOLD = 13;
    private static final float SMALL_ZOOM_THRESHOLD = 12;



    private static final float STATION_ZOOM_THRESHOLD = 15; /*  Остановки видны на высоте 14 и ниже  (15,16,17 и.тд)*/

    GoogleMap map;
    Resources resources;
    Context context;




    int display_width;

    int[] colors;


    float currentZoom;

    RouteDrawer routeDrawer = new RouteDrawer();
    BusDrawer busDrawer = new BusDrawer();
    StationDrawer stationDrawer = new StationDrawer();



    public MapDrawHelper(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
        this.resources = context.getResources();
        colors = resources.getIntArray(R.array.routecolors);

        display_width = context.getResources().getDisplayMetrics().widthPixels;

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (currentZoom != cameraPosition.zoom) {
                    busDrawer.forceRedraw();
                    currentZoom = cameraPosition.zoom;
                }

                if(stationDrawer.getStations()!=null){
                    stationDrawer.draw();
                }

            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

    }



    /**
     * Метод отрисовывает на карте маршруты
     */
    public void drawRoutes(ArrayList<Routes.Route> checkedRoute) {
        busDrawer.clearAllMarkers();  /*отчищаем все автобусы*/
        routeDrawer.setCheckedRoute(checkedRoute);
        routeDrawer.draw();
    }

    /**
     * Метод отрисовывает на карте автобусы
     * @param busses
     */
    public void drawBusses( Busses busses){
        busDrawer.setBusses(busses);
        busDrawer.draw();
    }

    /**
     * Метод отрисовывает на карте остановки
     * @param stations
     */
    public void drawStations(Stations stations){
        stationDrawer.setStations(stations);
        stationDrawer.draw();
    }






















    public class RouteDrawer{

        ArrayList<Map<String, Object>> lines = new ArrayList<>();
        ArrayList<Routes.Route> checked_route;

        public RouteDrawer(ArrayList<Routes.Route> checked_route) {
            this.checked_route = checked_route;
        }
        public RouteDrawer(){

        }

        private void clearAllLines() {
            for (int i = 0; i < lines.size(); i++) {
                Polyline line = ((Polyline) lines.get(i).get("line"));
                line.remove();
            }
            lines.clear();
        }

        public int findColorOfRoute(Busses.Bus bus) {

            for (int i = 0; i < lines.size(); i++) {
                Routes.Route route = (Routes.Route) lines.get(i).get("route");
                if (bus.getBusreportRouteId().equals(route.getBusreportRouteId())) {
                    return (int) lines.get(i).get("color");
                }
            }

            return -1;
        }

        public String findBusNumberByRoute(String StringBusReportId) {

            for (int i = 0; i < lines.size(); i++) {
                Routes.Route route = (Routes.Route) lines.get(i).get("route");
                if (StringBusReportId.equals(route.getBusreportRouteId())) {
                    return route.getRouteNumber();
                }
            }

            return "NaN";
        }

        public void draw() {
            clearAllLines();
            for (int i=0; i<checked_route.size(); i++) {
                Routes.Route route = checked_route.get(i);
                int color =colors[i];
                /**
                 * Ширина линии 1/80 от ширины экрана
                 */
                PolylineOptions options = new PolylineOptions().
                        width(display_width / 80).
                        color(color).
                        geodesic(true);

                options.addAll(route.getGoogleLocation());
                Polyline line = map.addPolyline(options);
                Map<String, Object> map = new HashMap<>();
                map.put("line", line);
                map.put("route", route);
                map.put("color", color);

                lines.add(map);
            }
        }

        public void setCheckedRoute(ArrayList<Routes.Route> checkedRoute) {
            this.checked_route = checkedRoute;
        }

    }



    public class BusDrawer{

        Busses busses;
        ArrayList<Map<String, Object>> markers = new ArrayList<>();

        public void setBusses(Busses busses){
            this.busses = busses;
        }

        public void draw() {
            try {
                for (Busses.Bus bus : busses.getAllBus()) {
                    MarkerOptions op = new MarkerOptions();
                    op.position(bus.getLatLon().toLatLng());
                    Integer direction = Integer.valueOf(bus.getDirection());
                    int color = routeDrawer.findColorOfRoute(bus);
                    if(color==-1){
                        continue;
                    }
                    float zoom = map.getCameraPosition().zoom;

                    Bitmap arrow = arrowBitmapFactory(zoom, direction, color, bus.getBusreportRouteId());
                    op.icon(BitmapDescriptorFactory.fromBitmap(arrow));

                    clearMarkerByBusName(bus);

                    Marker marker = map.addMarker(op);
                    Map<String, Object> map = new HashMap<>();
                    map.put("bus", bus);
                    map.put("marker", marker);
                    map.put("color", color);
                    markers.add(map);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Перерисовывает все маркеры
         */
        public void forceRedraw(){
            for (int i = 0; i < markers.size(); i++) {
                Marker marker = ((Marker) markers.get(i).get("marker"));
                Busses.Bus bus = ((Busses.Bus) markers.get(i).get("bus"));
                Integer direction = Integer.valueOf(bus.getDirection());
                int color = (int)markers.get(i).get("color");
                if(color==-1){
                    continue;
                }
                float zoom = map.getCameraPosition().zoom;

                Bitmap arrow = arrowBitmapFactory(zoom, direction, color, bus.getBusreportRouteId());
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(arrow));
            }

        }


        /**
         *  Рисует большой маркер
         * @param direction
         * @param color
         * @param busreportRouteId
         * @return
         */
        private Bitmap createBigestArrowBitmap(Integer direction, int color, String busreportRouteId) {
            /**
             * Ширина и высота берется из расчета 1/12 от ширины экрана
             */
            int WIDTH_BASE = display_width / 12;
            int HEIGHT_BASE = display_width / 12;


            Bitmap bm = Bitmap.createBitmap(WIDTH_BASE, HEIGHT_BASE, Bitmap.Config.ARGB_8888);
            Canvas canv = new Canvas(bm);

            drawArrowCircle(canv, color, direction, WIDTH_BASE, HEIGHT_BASE, CIRCLE_MODE.ARROW_CIRCLE);

            /**
             * Ширина текста 1/4 от базовой
             */


            String name = routeDrawer.findBusNumberByRoute(busreportRouteId);

            float textSize = WIDTH_BASE/4;

            Paint textPaint = new Paint();
            textPaint.setTextSize(textSize);
            textPaint.setFilterBitmap(true);

            Rect bounds = new Rect();
            textPaint.getTextBounds(name, 0, name.length(), bounds);
            int x = (canv.getWidth() / 2) - (bounds.width() / 2)-(name.length()/2);
            float y = (canv.getHeight() / 2) - (bounds.height() / 2) - textPaint.descent() - textPaint.ascent();
            canv.drawText(name, x, y, textPaint);

            return bm;
        }


        private Bitmap createPreMediumArrowBitmap(Integer direction, int color, String busreportRouteId){
            /**
             * Ширина и высота берется из расчета 1/15 от ширины экрана
             */
            int WIDTH_BASE = display_width / 15;
            int HEIGHT_BASE = display_width / 15;

            Bitmap bm = Bitmap.createBitmap(WIDTH_BASE, HEIGHT_BASE, Bitmap.Config.ARGB_8888);
            Canvas canv = new Canvas(bm);

            drawArrowCircle(canv, color,direction, WIDTH_BASE, HEIGHT_BASE,  CIRCLE_MODE.ARROW_CIRCLE);

            /**
             * Ширина текста 1/4 от базовой
             */

            String name = routeDrawer.findBusNumberByRoute(busreportRouteId);

            float textSize = WIDTH_BASE/4;
            Paint textPaint = new Paint();
            textPaint.setTextSize(textSize);
            textPaint.setFilterBitmap(true);



            Rect bounds = new Rect();
            textPaint.getTextBounds(name, 0, name.length(), bounds);
            int x = (canv.getWidth() / 2) - (bounds.width() / 2)-(name.length()/2);
            float y = (canv.getHeight() / 2) - (bounds.height() / 2) - textPaint.descent() - textPaint.ascent();
            canv.drawText(name, x, y, textPaint);

            return bm;
        }


        private Bitmap createMediumArrowBitmap(Integer direction, int color, String busreportRouteId){
            /**
             * Ширина и высота берется из расчета 1/25 от ширины экрана
             */
            int WIDTH_BASE = display_width / 20;
            int HEIGHT_BASE = display_width / 20;

            Bitmap bm = Bitmap.createBitmap(WIDTH_BASE, HEIGHT_BASE, Bitmap.Config.ARGB_8888);
            Canvas canv = new Canvas(bm);
            drawArrowCircle(canv, color, direction, WIDTH_BASE, HEIGHT_BASE, CIRCLE_MODE.ARROW_CIRCLE);
            return bm;
        }




        private Bitmap createSmallArrowBitmap(Integer direction, int color, String busreportRouteId){
            /**
             * Ширина и высота берется из расчета 1/25 от ширины экрана
             */
            int WIDTH_BASE = display_width / 35;
            int HEIGHT_BASE = display_width / 35;

            Bitmap bm = Bitmap.createBitmap(WIDTH_BASE, HEIGHT_BASE, Bitmap.Config.ARGB_8888);
            Canvas canv = new Canvas(bm);

            drawArrowCircle(canv, color, direction, WIDTH_BASE, HEIGHT_BASE, CIRCLE_MODE.CIRCLE);

            return bm;
        }



        private void drawArrowCircle(Canvas canv, int color, int direction, int base_width, int base_height, CIRCLE_MODE mode){

            Paint circlePaint = new Paint();
            circlePaint.setColor(Color.WHITE);
            circlePaint.setAntiAlias(true);
            circlePaint.setFilterBitmap(true);
            canv.drawCircle(base_width / 2, base_height / 2, base_width / 2 - 1, circlePaint);

            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            ColorFilter filter = new LightingColorFilter(color + Color.alpha(255), 1);
            paint.setColorFilter(filter);

            Bitmap arrowBitmap;
            if(mode==CIRCLE_MODE.CIRCLE){
                arrowBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker);
            }else{
                arrowBitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_a);
            }
            Bitmap scalable = Bitmap.createScaledBitmap(arrowBitmap, base_width, base_height, false);

            Matrix matrix = new Matrix();
            matrix.setRotate(direction, scalable.getWidth() / 2, scalable.getHeight() / 2);

            canv.setMatrix(matrix);
            canv.drawBitmap(scalable, 0, 0, paint);
            canv.setMatrix(null);

        }

        private Bitmap arrowBitmapFactory(float zoom, int direction, int color, String busreportRouteId){
            Bitmap arrow = null;
            if(zoom<=SMALL_ZOOM_THRESHOLD){
                arrow = createSmallArrowBitmap(direction, color, busreportRouteId);
            }else if((zoom>=SMALL_ZOOM_THRESHOLD) && ((zoom<=MEDIUM_ZOOM_THRESHOLD))) {
                arrow = createMediumArrowBitmap(direction, color, busreportRouteId);
            }else if((zoom>=MEDIUM_ZOOM_THRESHOLD) &&(zoom<=PREMEDIUM_ZOOM_THRESHOLD)  ){
                arrow = createPreMediumArrowBitmap(direction, color, busreportRouteId);
            }else {
                arrow = createBigestArrowBitmap(direction, color, busreportRouteId);
            }
            return arrow;
        }


        private void clearAllMarkers() {
            for (int i = 0; i < markers.size(); i++) {
                Marker marker = ((Marker) markers.get(i).get("marker"));
                marker.remove();
            }
            markers.clear();
        }

        private void clearMarkerByBusName(Busses.Bus bus) {
            Iterator<Map<String, Object>> iterator = markers.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> map = iterator.next();
                Busses.Bus existBus = (Busses.Bus) map.get("bus");
                if (bus.getName().equals(existBus.getName())) {
                    Marker marker = (Marker) map.get("marker");
                    marker.remove();
                    iterator.remove();
                }
            }
        }



    }


    public class StationDrawer{

        ArrayList<Map<String, Object>> drawed_station = new ArrayList<>();
        Stations stations;

        public void setStations(Stations stations){
            this.stations = stations;
        }
        public Stations getStations(){
            return this.stations;
        }

        public void draw(){
            clearAllStations();

            if(currentZoom>=STATION_ZOOM_THRESHOLD){
                Bitmap station_bitmap = createSmallStationBitmap();
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(station_bitmap);

                VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
                LatLngBounds bounds = visibleRegion.latLngBounds;

                for(int i=0; i<stations.size(); i++){
                    Stations.Station station = stations.get(i);
                    LatLng station_coords = station.getLatlon().toLatLng();
                    if(bounds.contains(station_coords)){
                        MarkerOptions op = new MarkerOptions();
                        op.position(station.getLatlon().toLatLng());
                        op.icon(descriptor);
                        Marker marker = map.addMarker(op);
                        Map<String, Object> map = new HashMap<>();
                        map.put("station", marker);
                        drawed_station.add(map);
                    }
                }
            }
        }

        private void clearAllStations(){
            for (int i = 0; i < drawed_station.size(); i++) {
                Marker marker = ((Marker) drawed_station.get(i).get("station"));
                marker.remove();
            }
            drawed_station.clear();
        }


        private Bitmap createSmallStationBitmap(){
            /**
             * Ширина и высота берется из расчета 1/25 от ширины экрана
             */
            int WIDTH_BASE = display_width / 20;
            int HEIGHT_BASE = display_width / 20;

            Bitmap bm = Bitmap.createBitmap(WIDTH_BASE, HEIGHT_BASE, Bitmap.Config.ARGB_8888);
            Canvas canv = new Canvas(bm);
            drawStationIcon(canv, WIDTH_BASE, HEIGHT_BASE);

            return bm;
        }

        private void drawStationIcon(Canvas canv, int base_width, int base_height){

            Bitmap stationBitmap;

            stationBitmap = BitmapFactory.decodeResource(resources, R.drawable.bus_station_icon);
            Bitmap scalable = Bitmap.createScaledBitmap(stationBitmap, base_width, base_height, false);

            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            canv.drawBitmap(scalable, 0, 0, paint);
        }


        private double distance(LatLng p1, LatLng p2){
            int R = 6371; /*Earth radius*/
            return (Math.acos(Math.sin(p1.latitude)*Math.sin(p2.latitude)+Math.cos(p1.latitude)*Math.cos(p2.latitude) * Math.cos(p2.longitude-p1.longitude)) * R)/10;
        }


    }

}
