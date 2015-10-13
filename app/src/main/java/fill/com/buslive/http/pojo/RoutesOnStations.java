package fill.com.buslive.http.pojo;

import java.util.ArrayList;

/**
 * Created by Fill on 14.10.2015.
 */
public class RoutesOnStations implements AbstractPOJO {

    ArrayList<Integer> routes_on_stations = new ArrayList<>();

    public ArrayList<Integer> getRoutes_on_stations() {
        return routes_on_stations;
    }

    public void setRoutes_on_stations(ArrayList<Integer> routes_on_stations) {
        this.routes_on_stations = routes_on_stations;
    }

    public boolean add(Integer object) {
        return routes_on_stations.add(object);
    }

    public Integer get(int index) {
        return routes_on_stations.get(index);
    }
}
