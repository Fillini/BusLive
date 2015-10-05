package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.Geocode;

/**
 * Created by Fill on 29.12.2014.
 */
public class GeocodeDeserializer implements JsonDeserializer<Geocode> {
    @Override
    public Geocode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Geocode geocode = new Geocode();
        try {
            JsonObject obj = json.getAsJsonObject()
                    .getAsJsonObject("response")
                    .getAsJsonObject("GeoObjectCollection")
                    .getAsJsonArray("featureMember")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("GeoObject")
                    .getAsJsonObject("Point");

            JsonPrimitive primitive = obj.getAsJsonPrimitive("pos");

            String[] latLong = primitive.toString().replace("\"", "").split(" ");

            float lon = Float.valueOf(latLong[0]);
            float lat = Float.valueOf(latLong[1]);
            geocode.setLatitude(lat);
            geocode.setLongitude(lon);

        }catch (Exception e){

            e.printStackTrace();


            geocode.setLatitude(0);
            geocode.setLongitude(0);
        }


        return geocode;
    }
}
