package fill.com.buslive.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Fill on 18.10.2015.
 */
public class LocationUtils {

    /**
     *  Вычисляет дистанцию между двумя точками в метрах
     * @param p1
     * @param p2
     * @return
     */
    public static double distance(LatLng p1, LatLng p2){
        int R = 6371; /*Earth radius*/
        return ((Math.acos(Math.sin(p1.latitude)*Math.sin(p2.latitude)+Math.cos(p1.latitude)*Math.cos(p2.latitude) * Math.cos(p2.longitude-p1.longitude)) * R)/10)*100;
    }



}
