package fill.com.buslive.event;

import fill.com.buslive.http.pojo.Stations;

/**
 * Created by Fill on 10.10.2015.
 */
public class ClickStationEvent {


    Stations.Station station;

    long start = System.currentTimeMillis();

    public Stations.Station getStation() {
        return station;
    }

    public void setStation(Stations.Station station) {
        this.station = station;
    }

    public long getStart() {
        return start;
    }
}
