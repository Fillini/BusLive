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

        int sequenceNumber;
        int routeId;
        int stationId;
        int cityId;
        boolean directionForward;

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public int getRouteId() {
            return routeId;
        }

        public void setRouteId(int routeId) {
            this.routeId = routeId;
        }

        public int getStationId() {
            return stationId;
        }

        public void setStationId(int stationId) {
            this.stationId = stationId;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
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
