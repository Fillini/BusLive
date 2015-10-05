package fill.com.buslive.utils;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Fill on 29.12.2014.
 */
public class LatLon implements Serializable{
    float lat;
    float lon;

    public LatLon(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }


    public LatLng toLatLng(){
       LatLng lt = new LatLng(lat, lon);
       return lt;
    }
}