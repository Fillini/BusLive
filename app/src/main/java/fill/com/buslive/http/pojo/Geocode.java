package fill.com.buslive.http.pojo;

/**
 * Created by Fill on 29.12.2014.
 */
public class Geocode implements AbstractPOJO {

    private float latitude;
    private float longitude;


    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
