package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;
import fill.com.buslive.utils.LatLon;

/**
 * Created by Fill on 29.12.2014.
 */
public class RoutesDeserializer implements JsonDeserializer<Routes> {


    @Override
    public Routes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Routes routes = new Routes();
        JsonArray jsonArray = json.getAsJsonArray();
        for(int i=0; i<jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Routes.Route route = new Routes.Route();
            route.setCityId(jsonObject.get("cityId").getAsString());
            route.setRouteName(jsonObject.get("routeName").getAsString());
            route.setRouteNumber(jsonObject.get("routeNumber").getAsString());
            route.setBussesOnRoute(jsonObject.get("bussesOnRoute").getAsString());
            route.setBusreportRouteId(jsonObject.get("busreportRouteId").getAsString());
            route.setLocation(parseLocation(jsonObject.get("location").getAsString()));
            routes.addRoute(route);
        }
        return routes;
    }



    private ArrayList<LatLon> parseLocation(String location){
        ArrayList<LatLon> arr = new ArrayList<LatLon>();
        /*------Разделяем на пары широта долгота  разделитель ', '-------*/
        String[] latlon = location.split(",");
        for(String loc:latlon){
            loc = loc.trim();
            String[] coord = loc.split(" "); // разделяем долготу и широту
            if(coord.length>1){
                LatLon latLon = new LatLon(Float.valueOf(coord[1]), Float.valueOf(coord[0]));
                arr.add(latLon);
            }
        }
        return arr;
    }

}
