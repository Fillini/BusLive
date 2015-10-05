package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.Countries;

/**
 * Created by Fill on 14.12.2014.
 */
public class CountriesDeserializer implements JsonDeserializer<Countries> {
    @Override
    public Countries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Countries countries = new Countries();

        JsonArray jsonArray = json.getAsJsonArray();

        for(int i=0; i<jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Countries.Country country = new Countries.Country();

            country.setId(jsonObject.get("id").getAsString());
            country.setName(jsonObject.get("name").getAsString());
            country.setCode(jsonObject.get("code").getAsString());
            countries.addCountry(country);
        }
        return countries;
    }
}
