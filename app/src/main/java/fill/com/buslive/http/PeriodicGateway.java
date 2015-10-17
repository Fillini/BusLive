package fill.com.buslive.http;

import android.content.Context;
import android.os.Handler;

import fill.com.buslive.http.pojo.Routes;

/**
 * Created by Fill on 13.01.2015.
 */
public class PeriodicGateway extends ServerGateway {

    /*Выбранные маршруты*/
    Routes checkedRoutes = new Routes();

    /* Период 5 секунд */
    static Integer PERIOD = 5000;
    /* Задержка перед запуском таймера 0 секунд */
    static Integer DELAY = 0;

    private boolean isStop = false;

    Handler getBussesHandler = new Handler();

    private boolean isGetPredictionStop = false;
    Handler getPredictionsHandler = new Handler();

    public PeriodicGateway(Context context, ResponseCallback callback) {
        super(context, callback);
    }

    /**
     * Запрос возвращает список автобусов на маршруте
     * @param checkedRoutes
     */
    public void startGetBusses(final Routes checkedRoutes){
        this.checkedRoutes = checkedRoutes;
        getBussesHandler.removeCallbacksAndMessages(null);
        getBussesHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Routes.Route route : checkedRoutes.getRoutes()) {
                    getBusses(route.getCityId(), route.getBusreportRouteId());
                }
                if (!isStop) {
                    getBussesHandler.postDelayed(this, PERIOD);
                }
            }
        }, DELAY);
    }

    public void startGetPredictions(final String city_id, final String station_id){
        getPredictionsHandler.removeCallbacksAndMessages(null);
        getPredictionsHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isGetPredictionStop){
                    getPredictions(city_id, station_id);
                    getPredictionsHandler.postDelayed(this, PERIOD*2);
                }
            }
        }, DELAY);
    }


    public void stopGetBusses(){
        isStop = true;
        getBussesHandler.removeCallbacksAndMessages(null);
    }

    public void pauseGetBusses(){
        isStop = true;
        getBussesHandler.removeCallbacksAndMessages(null);
    }

    public void restartGetBusses(){
        if(checkedRoutes!=null){
            isStop = false;
            startGetBusses(this.checkedRoutes);
        }
    }



    public void pauseGetPredictions(){
        isGetPredictionStop=true;
        getPredictionsHandler.removeCallbacksAndMessages(null);
    }


    public void addRoute(Routes.Route route){
        checkedRoutes.addRoute(route);
    }


    public void destroy(){
        isStop = true;
        getBussesHandler.removeCallbacksAndMessages(null);
        getPredictionsHandler.removeCallbacksAndMessages(null);
        checkedRoutes=null;
    }

}
