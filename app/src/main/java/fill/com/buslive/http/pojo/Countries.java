package fill.com.buslive.http.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fill on 14.12.2014.
 */
public class Countries implements AbstractPOJO{


    private ArrayList<Country> countries;

    public Countries() {
        this.countries = new ArrayList<Country>();
    }

    public void setCountries(ArrayList countries) {
        this.countries = countries;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public List<Map<String, Object>> getCountriesAsMap(){

        ArrayList<Map<String, Object>> arr = new ArrayList<Map<String,Object>>();
        for(Country country : getCountries()){
            Map<String, Object> map = country.getAsMap();
            arr.add(map);
        }
        return arr;
    }

    public String[] getAsStringArray(){
        ArrayList<String> arrayList = new ArrayList<String>();
        for(Country country: getCountries()){
            arrayList.add(country.getName());
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }
    public String[] getAsIntArray(){
        ArrayList<String> arrayList = new ArrayList<String>();
        for(Country country: getCountries()){
            arrayList.add(country.getId()+"");
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }

    public void addCountry(Country country){
        getCountries().add(country);
    }

    public Country get(int index){
        return countries.get(index);
    }
    public int size(){
        return countries.size();
    }

    /**
     * Created by Fill on 14.12.2014.
     */
    public static class Country implements AbstractPOJO {

        public String id;

        public String name;

        public String code;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCode(String code) {
            this.code = code;
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
