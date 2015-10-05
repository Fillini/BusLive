package fill.com.buslive.http;

import android.content.Context;

import fill.com.buslive.R;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Geocode;
import fill.com.buslive.utils.L;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Fill on 29.12.2014.
 */
public class GeocodingGateway extends Gateway {


    public static final String GEOCODING_PREFIX = "?format=json&results=1";

    Retrofit retrofit;

    public GeocodingGateway(Context context, ResponseCallback callback) {
        super(context, callback);
        serviceUrl = context.getResources().getString(R.string.geocoding_url);

        retrofit = new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addConverterFactory(GsonConverterFactory.create(new Deserializer().getGson()))
                .build();

    }


    public void getGeocodeInfo(String country, String city){
        String name = country+"+"+city;
        GeocodeService service = retrofit.create(GeocodeService.class);
        executeCallback(service.getGeocode(name));
    }


    private <T extends AbstractPOJO> void executeCallback(Call<T> call){
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Response<T> response) {
                callback.onSucces(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });

    }


    interface GeocodeService{
        @GET(GEOCODING_PREFIX)
        Call<Geocode> getGeocode(@Query("geocode") String name);
    }

}
