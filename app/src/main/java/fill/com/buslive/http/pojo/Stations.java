package fill.com.buslive.http.pojo;

import java.util.ArrayList;

import fill.com.buslive.utils.LatLon;

/**
 * Created by Fill on 29.12.2014.
 */
public class Stations implements AbstractPOJO {

    ArrayList<Station> stations = new ArrayList<>();


    public Stations() {
        this.stations = new ArrayList<Station>();
    }


    public void addStation(Station station) {
        stations.add(station);
    }

    public Station getStationById(String id) {

        for (Station station : stations) {
            if (station.getId().equals(id)) {
                return station;
            }
        }

        return null;
    }

    public ArrayList<Station> getStations(){
        return stations;
    }


    public Station get(int index) {
        return stations.get(index);
    }

    public int size() {
        return stations.size();
    }


    public Station getStationByName(String name) {
        for (Station station : stations) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;
    }

    /**
     * Created by Fill on 29.12.2014.
     * Класс описывает сущность "Остановка"
     */
    public static class Station implements AbstractPOJO {


        String name;
        String id;
        String description;
        LatLon latlon;
        String cityId;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LatLon getLatlon() {
            return latlon;
        }

        public void setLatlon(LatLon latlon) {
            this.latlon = latlon;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }


        public float getLat() {
            return latlon.getLat();
        }

        public float getLon() {
            return latlon.getLon();
        }
    }
}
