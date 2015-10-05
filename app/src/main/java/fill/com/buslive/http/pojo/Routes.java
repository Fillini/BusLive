package fill.com.buslive.http.pojo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import fill.com.buslive.utils.LatLon;

/**
 * Created by Fill on 29.12.2014.
 */
public class Routes implements AbstractPOJO{


    private ArrayList<Route> routes;

    public Routes() {
        this.routes = new ArrayList<Route>();
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route){
        routes.add(route);
    }


    public int size() {
        return routes.size();
    }

    public Route get(int index) {
        return routes.get(index);
    }

    public Route getRouteByRouteNumber(String routeNumber){
        for(Route route:routes){
            if(route.getRouteNumber().equals(routeNumber)){
                return route;
            }
        }
        return null;
    }

    /**
     * Created by Fill on 29.12.2014.
     */
    public static class Route implements AbstractPOJO {


        private ArrayList<LatLon> location;

        private String routeNumber;
        private String routeName;
        private String cityId;
        private String busreportRouteId;
        private String bussesOnRoute;


        public Route() {
            location = new ArrayList<LatLon>();
        }

        public ArrayList<LatLon> getLocation() {
            return location;
        }
        public ArrayList<LatLng> getGoogleLocation(){
            ArrayList<LatLng> list = new ArrayList<>();

            for(LatLon latLon:getLocation()){
                list.add(latLon.toLatLng());
            }

            return list;
        }

        public void setLocation(ArrayList<LatLon> location) {
            this.location = location;
        }


        /**
         * Номер маршрута
         * */
        public String getRouteNumber() {
            return routeNumber;
        }

        public void setRouteNumber(String routeNumber) {
            this.routeNumber = routeNumber;
        }

        /**
         * Название маршрута
         */
        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getBusreportRouteId() {
            return busreportRouteId;
        }

        public void setBusreportRouteId(String busreportRouteId) {
            this.busreportRouteId = busreportRouteId;
        }

        public String getBussesOnRoute() {
            return bussesOnRoute;
        }

        public void setBussesOnRoute(String bussesOnRoute) {
            this.bussesOnRoute = bussesOnRoute;
        }

    }
}
