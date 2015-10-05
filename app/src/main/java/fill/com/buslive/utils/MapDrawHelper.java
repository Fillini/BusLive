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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fill.com.buslive.R;
import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Routes;

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

    GoogleMap map;
    Resources resources;
    Context context;

    ArrayList<Map<String, Object>> markers = new ArrayList<Map<String, Object>>();

    ArrayList<Map<String, Object>> lines = new ArrayList<>();

    int display_width;


    int[] colors;


    Busses busses;
    float currentZoom;


    public MapDrawHelper(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
        this.resources = context.getResources();
        colors = resources.getIntArray(R.array.routecolors);

        display_width = context.getResources().getDisplayMetrics().widthPixels;

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(currentZoom!=cameraPosition.zoom){
                    forceRedrawBusses();
                    currentZoom=cameraPosition.zoom;
                }

            }
        });


    }

    public void drawBusses(Busses busses) {

        this.busses = busses;
        try {
            for (Busses.Bus bus : busses.getAllBus()) {
                MarkerOptions op = new MarkerOptions();
                op.position(bus.getLatLon().toLatLng());
                Integer direction = Integer.valueOf(bus.getDirection());
                int color = findColorOfRoute(bus);
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

                markers.add(map);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Перерисовывает все маркеры
     */
    private void forceRedrawBusses(){
        for (int i = 0; i < markers.size(); i++) {
            Marker marker = ((Marker) markers.get(i).get("marker"));
            Busses.Bus bus = ((Busses.Bus) markers.get(i).get("bus"));
            Integer direction = Integer.valueOf(bus.getDirection());
            int color = findColorOfRoute(bus);
            if(color==-1){
                continue;
            }
            float zoom = map.getCameraPosition().zoom;

            Bitmap arrow = arrowBitmapFactory(zoom, direction, color, bus.getBusreportRouteId());
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(arrow));
        }

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

    private void clearAllLines() {
        for (int i = 0; i < lines.size(); i++) {
            Polyline line = ((Polyline) lines.get(i).get("line"));
            line.remove();
        }
        lines.clear();
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


    private int findColorOfRoute(Busses.Bus bus) {

        for (int i = 0; i < lines.size(); i++) {
            Routes.Route route = (Routes.Route) lines.get(i).get("route");
            if (bus.getBusreportRouteId().equals(route.getBusreportRouteId())) {
                return (int) lines.get(i).get("color");
            }
        }

        return -1;
    }

    private String findBusNumberByRoute(String StringBusReportId) {

        for (int i = 0; i < lines.size(); i++) {
            Routes.Route route = (Routes.Route) lines.get(i).get("route");
            if (StringBusReportId.equals(route.getBusreportRouteId())) {
                return route.getRouteNumber();
            }
        }

        return "NaN";
    }


    /**
     * Отчищаем маркеры в зависимости от выбраных маршрутов
     */
    private void clearMarkersByRoutes() {

        ArrayList<Map<String, Object>> existingMarkers = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> linesMap : lines) {

            Routes.Route route = (Routes.Route) linesMap.get("route");

            for (Map<String, Object> markersMap : markers) {

                Busses.Bus bus = (Busses.Bus) markersMap.get("bus");

                if (route.getBusreportRouteId().equals(bus.getBusreportRouteId())) {
                    existingMarkers.add(markersMap);
                }
            }
        }

        Iterator<Map<String, Object>> markersIterator = markers.iterator();

        while (markersIterator.hasNext()) {
            Map<String, Object> m = markersIterator.next();
            if (!existingMarkers.contains(m)) {
                Marker marker = ((Marker) m.get("marker"));
                marker.remove();
                markersIterator.remove();
            }
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

        drawArrowCircle(canv, color,direction, WIDTH_BASE, HEIGHT_BASE,  CIRCLE_MODE.ARROW_CIRCLE);

        /**
         * Ширина текста 1/4 от базовой
         */
        Paint textPaint = new Paint();
        textPaint.setTextSize(WIDTH_BASE / 4);
        textPaint.setFilterBitmap(true);

        String name = findBusNumberByRoute(busreportRouteId);

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
        Paint textPaint = new Paint();
        textPaint.setTextSize(WIDTH_BASE / 4);
        textPaint.setFilterBitmap(true);


        String name = findBusNumberByRoute(busreportRouteId);

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








    /**
     * Метод отрисовывает на карте маршруты
     */
    public void drawRoutes(ArrayList<Routes.Route> checkedRoute) {

        clearAllLines();

        clearAllMarkers();
        for (int i=0; i<checkedRoute.size(); i++) {
            Routes.Route route = checkedRoute.get(i);
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


}
