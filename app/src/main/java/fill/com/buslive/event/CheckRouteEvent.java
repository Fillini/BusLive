package fill.com.buslive.event;

import java.util.ArrayList;

import fill.com.buslive.http.pojo.Routes;

/**
 * Created by Fill on 07.10.2015.
 */
public class CheckRouteEvent {


    ArrayList<Routes.Route> checked_route;

    long time = System.currentTimeMillis();


    public ArrayList<Routes.Route> getChecked_route() {
        return checked_route;
    }

    public void setChecked_route(ArrayList<Routes.Route> checked_route) {
        this.checked_route = checked_route;
    }

    public long getTime(){
        return time;
    }
}
