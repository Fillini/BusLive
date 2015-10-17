package fill.com.buslive.http.pojo;



import java.util.ArrayList;

/**
 * Created by Fill on 17.10.2015.
 */
public class Predictions implements AbstractPOJO {

    ArrayList<Prediction> predictions = new ArrayList<>();

    public ArrayList<Prediction> getPredictions() {
        return predictions;
    }
    public void setPredictions(ArrayList<Prediction> predictions) {
        this.predictions = predictions;
    }

    public void add(Prediction prediction){
        this.predictions.add(prediction);
    }
    public Prediction get(int index){
        return this.predictions.get(index);
    }
    public boolean contains(Prediction prediction){
        return this.predictions.contains(prediction);
    }

    public static class Prediction implements AbstractPOJO{

        String speed;
        String routeId;
        String stationId;
        boolean reverse;
        String avgSpeed;
        String busIMEI;
        String distance;
        String generatedTime;
        boolean mainPrediction;
        String messageTime;
        String prediction;


        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
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

        public boolean isReverse() {
            return reverse;
        }

        public void setReverse(boolean reverse) {
            this.reverse = reverse;
        }

        public String getAvgSpeed() {
            return avgSpeed;
        }

        public void setAvgSpeed(String avgSpeed) {
            this.avgSpeed = avgSpeed;
        }

        public String getBusIMEI() {
            return busIMEI;
        }

        public void setBusIMEI(String busIMEI) {
            this.busIMEI = busIMEI;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getGeneratedTime() {
            return generatedTime;
        }

        public void setGeneratedTime(String generatedTime) {
            this.generatedTime = generatedTime;
        }

        public boolean isMainPrediction() {
            return mainPrediction;
        }

        public void setMainPrediction(boolean mainPrediction) {
            this.mainPrediction = mainPrediction;
        }

        public String getMessageTime() {
            return messageTime;
        }

        public void setMessageTime(String messageTime) {
            this.messageTime = messageTime;
        }

        public String getPrediction() {
            return prediction;
        }

        public void setPrediction(String prediction) {
            this.prediction = prediction;
        }
    }







}
