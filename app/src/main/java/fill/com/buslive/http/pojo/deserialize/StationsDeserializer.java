package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.Stations;
import fill.com.buslive.utils.LatLon;

/**
 * Created by Fill on 29.12.2014.
 */
public class StationsDeserializer implements JsonDeserializer<Stations> {
    @Override
    public Stations deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Stations stations = new Stations();


        JsonArray jsonArray = json.getAsJsonArray();
        for(int i=0; i<jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

            Stations.Station station = new Stations.Station();
            station.setCityId(jsonObject.get("cityId").getAsString());
            station.setName(jsonObject.get("name").getAsString());
            station.setId(jsonObject.get("id").getAsString());

            LatLon latlon = new LatLon(Float.valueOf(jsonObject.get("lat").getAsFloat()), Float.valueOf(jsonObject.get("lon").getAsFloat()));
            station.setLatlon(latlon);

            stations.addStation(station);
        }


        return stations;
    }
}
