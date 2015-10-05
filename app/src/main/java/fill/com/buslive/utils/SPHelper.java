package fill.com.buslive.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;

/**
 * Created by Fill on 12.12.2014.
 */
public class SPHelper {

    Context context;

    static SPHelper instance;

    SharedPreferences spref;

    private String country_name;
    private Integer country;
    private boolean isFirstRun;


    static String COUNTRY_NAME = "country_name";
    static String COUNTRY_ID = "country";
    static String CITY_NAME = "city_name";
    static String CITY = "city";
    static String COORDS = "coords";

    static  String IS_FIRST_RUN = "is_first_run";


    synchronized public static SPHelper getInstance(Context context){
        if(instance == null){
            instance = new SPHelper(context);
        }
        return instance;
    }

    private SPHelper(Context context) {
        this.context = context;
        spref = PreferenceManager.getDefaultSharedPreferences(context);
    }


    private void setString(String key, String value){
        spref.edit().putString(key, value).apply();
    }

    private void setInt(String key, int value){
        spref.edit().putInt(key, value).apply();
    }
    private void setBoolean(String key, boolean value){
        spref.edit().putBoolean(key, value).apply();
    }

    public String getCountry_name() {
        return getString(COUNTRY_NAME);
    }

    public void setCountry_name(String country_name) {
        setString(COUNTRY_NAME, country_name);
    }

    public String getCountry() {
       return getString(COUNTRY_ID);
    }

    public boolean isCountryExists(){
        if(getCountry().equals("")){
            return false;
        }
        return true;
    }

    public void setCountry(String country) {
        setString(COUNTRY_ID, country);
    }

    public String getCity_name(){
        return getString(CITY_NAME);
    }
    public  void setCity_name(String city_name){
        setString(CITY_NAME, city_name);
    }

    public String getCity(){
        return getString(CITY);
    }

    public  void setCity(String city){
        setString(CITY, city);
    }

    public boolean isCityExists(){
        if(getCity().equals("")){
            return false;
        }
        return true;
    }



    public String getCoords(){
        return getString(COORDS);
    }

    public void setCoords(String coords){
        setString(COORDS, coords);
    }


    private String getString(String key){
        return spref.getString(key, "");
    }
    private int getInt(String key){
        return spref.getInt(key, 0);
    }

    private boolean getBoolean(String key){
        return spref.getBoolean(key, false);
    }


    public boolean isSettingsExists(){
        return isCountryExists() && isCityExists();
    }

    public boolean isFirstRun(){
        if(!spref.contains(IS_FIRST_RUN)){
            return true;
        }
        return getBoolean(IS_FIRST_RUN);
    }
    public void setIsFirstRun(boolean value){
        setBoolean(IS_FIRST_RUN, value);
    }

}
