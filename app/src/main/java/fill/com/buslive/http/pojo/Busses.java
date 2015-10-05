package fill.com.buslive.http.pojo;

import java.util.ArrayList;

import fill.com.buslive.utils.LatLon;

/**
 * Created by Fill on 29.12.2014.
 */
public class Busses implements AbstractPOJO {



    ArrayList<Bus> busses;

    public Busses() {
        this.busses = new ArrayList<Bus>();
    }


    public void addBus(Bus bus){
        busses.add(bus);
    }


    public Bus get(int index){
        return busses.get(index);
    }
    public ArrayList<Bus> getAllBus(){
        return busses;
    }

    public int size() {
        return busses.size();
    }

    /**
     * Created by Fill on 30.12.2014.
     */
    public static class Bus implements AbstractPOJO {

        String name;
        String direction;
        String imei;
        LatLon latLon;
        String speed;
        String cityId;
        String busreportRouteId;
        Boolean offline;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public LatLon getLatLon() {
            return latLon;
        }

        public void setLatLon(LatLon latLon) {
            this.latLon = latLon;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
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

        public Boolean getOffline() {
            return offline;
        }

        public void setOffline(Boolean offline) {
            this.offline = offline;
        }
    }
}
