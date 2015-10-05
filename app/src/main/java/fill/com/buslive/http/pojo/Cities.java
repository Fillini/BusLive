package fill.com.buslive.http.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fill on 15.12.2014.
 */
public class Cities implements AbstractPOJO {

    private ArrayList<City> cities;

    public Cities() {
        this.cities = new ArrayList<City>();
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public List<Map<String, Object>> getCitiesAsMap(){

        ArrayList<Map<String, Object>> arr = new ArrayList<Map<String,Object>>();
        for(City city : getCities()){
            Map<String, Object> map = city.getAsMap();
            arr.add(map);
        }
        return arr;
    }

    public String[] getAsStringArray(){
        ArrayList<String> arrayList = new ArrayList<String>();
        for(City city: getCities()){
            arrayList.add(city.getName());
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }
    public String[] getAsIntArray(){
        ArrayList<String> arrayList = new ArrayList<String>();
        for(City city: getCities()){
            arrayList.add(city.getId()+"");
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }


    public void addCity(City city){
        cities.add(city);
    }

    public int size() {
        return cities.size();
    }

    public City get(int index) {
        return cities.get(index);
    }

    /**
     * Created by Fill on 15.12.2014.
     */
    public static class City implements AbstractPOJO {

        public String name;

        public String id;

        public String serviceURL;


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

        public String getServiceURL() {
            return serviceURL;
        }

        public void setServiceURL(String serviceURL) {
            this.serviceURL = serviceURL;
        }

        public Map<String, Object> getAsMap() {
            Field[] fields = getClass().getFields();
            Map<String, Object> map = new HashMap<String, Object>();
            try{
                for (Field field: fields){
                    map.put(field.getName(), field.get(this));
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }

            return map;
        }
    }
}
