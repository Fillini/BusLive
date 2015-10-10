package fill.com.buslive.http.pojo;

import java.util.ArrayList;

/**
 * Created by Fill on 10.10.2015.
 */
public class RouteStations implements AbstractPOJO {


    private ArrayList<RouteStation> routeStations;


    public RouteStations() {
        this.routeStations = new ArrayList<>();
    }


    public ArrayList<RouteStation> getRouteStations() {
        return routeStations;
    }

    public void setRouteStations(ArrayList<RouteStation> routeStations) {
        this.routeStations = routeStations;
    }

    public void addRouteStation(RouteStation routeStation){
        this.routeStations.add(routeStation);
    }


    public static class RouteStation implements AbstractPOJO{

        String sequenceNumber;
        String routeId;
        String stationId;
        String cityId;
        boolean directionForward;

        public String getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(String sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public String getRouteId() {
            return routeId;
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public String getStationId() {
            return stationId;
        }

        public void setStationId(String stationId) {
            this.stationId = stationId;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public boolean isDirectionForward() {
            return directionForward;
        }

        public void setDirectionForward(boolean directionForward) {
            this.directionForward = directionForward;
        }
    }


}
