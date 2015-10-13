package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.RoutesOnStations;

/**
 * Created by Fill on 14.10.2015.
 */
public class RoutesOnStationsDeserializer implements JsonDeserializer<RoutesOnStations> {


    @Override
    public RoutesOnStations deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        RoutesOnStations routesOnStations = new RoutesOnStations();

        JsonArray arr = json.getAsJsonArray();
        for(JsonElement el : arr){
            routesOnStations.add(el.getAsInt());
        }

        return routesOnStations;
    }
}
