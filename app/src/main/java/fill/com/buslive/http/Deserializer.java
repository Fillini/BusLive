package fill.com.buslive.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Cities;
import fill.com.buslive.http.pojo.Countries;
import fill.com.buslive.http.pojo.Geocode;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.http.pojo.Stations;
import fill.com.buslive.http.pojo.deserialize.BussesDeserializer;
import fill.com.buslive.http.pojo.deserialize.CitiesDeserializer;
import fill.com.buslive.http.pojo.deserialize.CountriesDeserializer;
import fill.com.buslive.http.pojo.deserialize.GeocodeDeserializer;
import fill.com.buslive.http.pojo.deserialize.RoutesDeserializer;
import fill.com.buslive.http.pojo.deserialize.StationsDeserializer;


public class Deserializer {

    public Gson getGson(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Countries.class, new CountriesDeserializer())
                .registerTypeAdapter(Cities.class, new CitiesDeserializer())
                .registerTypeAdapter(Geocode.class, new GeocodeDeserializer())
                .registerTypeAdapter(Routes.class, new RoutesDeserializer())
                .registerTypeAdapter(Stations.class, new StationsDeserializer())
                .registerTypeAdapter(Busses.class, new BussesDeserializer())
                .create();
        return gson;
    }


}
