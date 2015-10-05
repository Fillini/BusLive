package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.Cities;

/**
 * Created by Fill on 15.12.2014.
 */
public class CitiesDeserializer implements JsonDeserializer<Cities> {

    @Override
    public Cities deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Cities cities = new Cities();
        JsonArray jsonArray = json.getAsJsonArray();
        for(int i=0; i<jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Cities.City city = new Cities.City();
            city.setId(jsonObject.get("id").getAsString());
            city.setName(jsonObject.get("name").getAsString());
            city.setServiceURL(jsonObject.get("serviceURL").getAsString());
            cities.addCity(city);
        }

        return cities;
    }
}
