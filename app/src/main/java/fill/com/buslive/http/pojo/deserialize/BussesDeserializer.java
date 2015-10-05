package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.utils.LatLon;

/**
 * Created by Fill on 30.12.2014.
 */
public class BussesDeserializer implements JsonDeserializer<Busses> {

    @Override
    public Busses deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Busses busses = new Busses();

        JsonArray jsonArray = json.getAsJsonArray();
        for(int i=0; i<jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Busses.Bus bus = new Busses.Bus();
            bus.setName(jsonObject.get("name").getAsString());
            bus.setDirection(jsonObject.get("direction").getAsString());
            bus.setImei(jsonObject.get("imei").getAsString());
            bus.setSpeed(jsonObject.get("speed").getAsString());
            bus.setCityId(jsonObject.get("cityId").getAsString());
            bus.setBusreportRouteId(jsonObject.get("busreportRouteId").getAsString());
            bus.setOffline(jsonObject.get("offline").getAsBoolean());
            LatLon latLon = new LatLon(jsonObject.get("lat").getAsFloat(), jsonObject.get("lon").getAsFloat());
            bus.setLatLon(latLon);
            busses.addBus(bus);
        }
        return busses;
    }
}
