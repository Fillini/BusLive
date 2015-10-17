package fill.com.buslive.http.pojo.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fill.com.buslive.http.pojo.Predictions;

/**
 * Created by Fill on 17.10.2015.
 */
public class PredictionsDeserializer implements JsonDeserializer<Predictions> {
    @Override
    public Predictions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Predictions predictions = new Predictions();

        JsonArray arr = json.getAsJsonArray();
        for(JsonElement el: arr){
            JsonObject obj = el.getAsJsonObject();
            String speed = obj.get("speed").getAsString();
            String routeId = obj.get("routeId").getAsString();
            String stationId = obj.get("stationId").getAsString();
            boolean reverse = obj.get("reverse").getAsBoolean();
            String avgSpeed = obj.get("avgSpeed").getAsString();
            String busIMEI = obj.get("busIMEI").getAsString();
            String distance = obj.get("distance").getAsString();
            String generatedTime = obj.get("generatedTime").getAsString();
            boolean mainPrediction = obj.get("mainPrediction").getAsBoolean();
            String messageTime = obj.get("messageTime").getAsString();
            String prediction = obj.get("prediction").getAsString();

            Predictions.Prediction pre = new Predictions.Prediction();
            pre.setSpeed(speed);
            pre.setRouteId(routeId);
            pre.setStationId(stationId);
            pre.setReverse(reverse);
            pre.setAvgSpeed(avgSpeed);
            pre.setBusIMEI(busIMEI);
            pre.setDistance(distance);
            pre.setGeneratedTime(generatedTime);
            pre.setMainPrediction(mainPrediction);
            pre.setMessageTime(messageTime);
            pre.setPrediction(prediction);

            predictions.add(pre);
        }


        return predictions;
    }
}
