package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.RouteStations;

/**
 * Created by Fill on 10.10.2015.
 */
public class RouteStationsDeserializer implements JsonDeserializer<RouteStations> {
    @Override
    public RouteStations deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        RouteStations routeStations = new RouteStations();
        JsonArray array = json.getAsJsonArray();
        for(JsonElement el: array){
            JsonObject obj = el.getAsJsonObject();
            RouteStations.RouteStation routeStation = new RouteStations.RouteStation();
            int sequenceNumber = obj.get("sequenceNumber").getAsInt();
            int routeId = obj.get("routeId").getAsInt();
            int stationId = obj.get("stationId").getAsInt();
            int cityId = obj.get("cityId").getAsInt();
            boolean directionForward = obj.get("directionForward").getAsBoolean();
            routeStation.setSequenceNumber(sequenceNumber);
            routeStation.setRouteId(routeId);
            routeStation.setStationId(stationId);
            routeStation.setCityId(cityId);
            routeStation.setDirectionForward(directionForward);
            routeStations.addRouteStation(routeStation);
        }
        return routeStations;
    }
}
