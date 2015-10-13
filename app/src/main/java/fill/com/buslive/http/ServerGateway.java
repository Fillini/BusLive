package fill.com.buslive.http;

import android.content.Context;
import android.os.Environment;

import com.squareup.okhttp.Cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fill.com.buslive.R;

import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Busses;
import fill.com.buslive.http.pojo.Cities;
import fill.com.buslive.http.pojo.Countries;
import fill.com.buslive.http.pojo.RouteStations;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.http.pojo.RoutesOnStations;
import fill.com.buslive.http.pojo.Stations;
import fill.com.buslive.utils.L;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Fill on 11.12.2014.
 */

public class ServerGateway extends Gateway {

    public static final String COUNTRIES_PREFIX = "/countries";
    public static final String CITIES_PREFIX = "/countries/{country_id}/cities";
    public static final String ROUTES_PREFIX = "/cities/{city_id}/routes/";
    public static final String STATIONS_PREFIX = "/cities/{city_id}/stations/";
    public static final String BUSSES_PREFIX = "/cities/{city_id}/routes/{route_id}/busses";
    public static final String ROUTESTATIONS_PREFIX = "/cities/{city_id}/routestations";
    public static final String ROUTES_ON_STATIONS_PREFIX = "/cities/{city_id}/stations/{station_id}/routesatstation";




    Retrofit retrofit;
    Cache cache;
    BusliveService service;
    int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static final String SDCARD_FOLDER = Environment.getExternalStorageDirectory() + "/Android/data/%s/files/";

    public ServerGateway(Context context, ResponseCallback callback) {
        super(context, callback);

        serviceUrl = context.getResources().getString(R.string.serviceurl);
        retrofit = new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addConverterFactory(GsonConverterFactory.create(new Deserializer().getGson()))
                .build();

        File cahceDirectory = new File(String.format(SDCARD_FOLDER, context.getPackageName()));
        cache = new Cache(cahceDirectory, cacheSize);
        retrofit.client().setCache(cache);

        service = retrofit.create(BusliveService.class);
    }

    /**
     * Возвращает список стран
     */
    public void getCountries(){
        executeCallback(service.getCountries());
    }


    /**
     * Возвращает список городов
     * @param country_id - идентификатор страны
     */
    public void getCities(String country_id){
        executeCallback(service.getCities(country_id));
    }



    /**
     * Возвращает список всех маршрутов в городе
     * @param city_id - идентификатор города
     */
    public void getRoutes(String city_id){
        executeCallback(service.getRoutes(city_id));
    }

    /**
     * Возвращает список всех остановок в городе
     * @param city_id - идентификатор города
     */
    public void getStations(String city_id){
        executeCallback(service.getStations(city_id));
    }



    public void getRouteStations(String city_id){
        executeCallback(service.getRouteStations(city_id));
    }


    /**
     * Возвращает список автобусов по идентификатору маршрута
     * @param city_id - - идентификатор города
     * @param busreportRouteId - идентификатор маршрута (идентивикатор у всех автобусов на этом маршруте одинаков)
     */
    public void getBusses(String city_id, String busreportRouteId){
        executeCallback(service.getBusses(city_id, busreportRouteId));
    }


    public void getRoutesOnStations(String city_id, String station_id){
        executeCallback(service.getRoutesOnStations(city_id, station_id));
    }


    private <T extends AbstractPOJO> void executeCallback(Call<T> call){

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Response<T> response) {
                callback.onSucces(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface BusliveService{
        @GET(COUNTRIES_PREFIX)
        Call<Countries> getCountries();

        @GET(CITIES_PREFIX)
        Call<Cities> getCities(@Path("country_id") String country_id);

        @GET(ROUTES_PREFIX)
        Call<Routes> getRoutes(@Path("city_id") String city_id);


        @GET(STATIONS_PREFIX)
        Call<Stations>  getStations(@Path("city_id") String city_id);

        @GET(ROUTESTATIONS_PREFIX)
        Call<RouteStations>  getRouteStations(@Path("city_id") String city_id);


        @GET(BUSSES_PREFIX)
        Call<Busses> getBusses(@Path("city_id") String city_id, @Path("route_id") String busreportRouteId);

        /**
         *  Выводит маршруты проходящие на данной остановке
          * @return
         */
        @GET(ROUTES_ON_STATIONS_PREFIX)
        Call<RoutesOnStations> getRoutesOnStations(@Path("city_id") String city_id, @Path("station_id") String station_id);
    }

}
