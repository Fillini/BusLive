package fill.com.buslive.http;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashSet;

import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;

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

    Handler handler = new Handler();

    public PeriodicGateway(Context context, ResponseCallback callback) {
        super(context, callback);
    }

    /**
     * Запрос возвращает список автобусов на маршруте
     * @param checkedRoutes
     */
    public void startGetBusses(final Routes checkedRoutes){
        this.checkedRoutes = checkedRoutes;
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Routes.Route route : checkedRoutes.getRoutes()) {
                    getBusses(route.getCityId(), route.getBusreportRouteId());
                }
                if (!isStop) {
                    handler.postDelayed(this, PERIOD);
                }
            }
        }, DELAY);
    }

    public void stopGetBusses(){
        isStop = true;
        handler.removeCallbacksAndMessages(null);
    }

    public void pauseGetBusses(){
        isStop = true;
        handler.removeCallbacksAndMessages(null);
    }

    public void restartGetBusses(){
        if(checkedRoutes!=null){
            isStop = false;
            startGetBusses(this.checkedRoutes);
        }
    }

    public void addRoute(Routes.Route route){
        checkedRoutes.addRoute(route);
    }


    public void destroy(){
        isStop = true;
        handler.removeCallbacksAndMessages(null);
        checkedRoutes=null;
    }

}
