package fill.com.buslive;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fill.com.buslive.http.ResponseCallback;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Cities;
import fill.com.buslive.http.pojo.Countries;
import fill.com.buslive.utils.L;
import fill.com.buslive.utils.SPHelper;


public class SettingsActivity extends PreferenceActivity implements ResponseCallback {



    private AppCompatDelegate mDelegate;


    public ServerGateway gateway;

    PreferenceScreen rootScreen;
    ListPreference countryList;
    ListPreference cityList;

    Preference rating_preference;


    /*-------Флаг указывающий переворачивался экран или нет--------*/
    private boolean isRotated = false;


    /*--------Флаг указывающий что не установлены настройки города и страны.
              Если один из этих параметров указан, значит уже не первый раз запускали
     ------*/


    private boolean isFirstRun;

    Countries countries;
    Cities cities;


    private SPHelper spHelper;




    static final String CITIES_KEY = "cities";
    static final String COUNTRIES_KEY = "countries";


    static final String PREFERENCE_COUNTRY_KEY = "country";
    static final String PREFERENCE_CITY_KEY = "city";



    static final int MENU_BACK_ITEM_ID = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        gateway = new ServerGateway(this, this);
        spHelper = SPHelper.getInstance(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        isFirstRun = spHelper.isFirstRun();


        rootScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(rootScreen);

        countryList = new ListPreference(this);
        countryList.setKey(PREFERENCE_COUNTRY_KEY);
        countryList.setTitle(R.string.choose_country);
        countryList.setSummary(spHelper.getCountry_name());
        countryList.setEntries(new String[]{});
        countryList.setEntryValues(new String[]{});
        countryList.setDialogTitle(R.string.choose_country);
        countryList.setEnabled(false);

        countryList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (!spHelper.getCountry().equals(newValue)) {
                    cityList.setEnabled(false);
                    gateway.getCities(newValue.toString());
                    CharSequence[] values = countryList.getEntryValues();
                    CharSequence[] entries = countryList.getEntries();
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equals(newValue)) {
                            countryList.setSummary(entries[i]);
                            spHelper.setCountry_name(entries[i].toString());
                            spHelper.setCity("");
                        }
                    }
                }
                return true;
            }
        });

        rootScreen.addPreference(countryList);

        cityList = new ListPreference(this);
        cityList .setKey(PREFERENCE_CITY_KEY);
        cityList .setTitle(R.string.choose_city);
        cityList .setSummary(spHelper.getCity_name());
        cityList .setEntries(new String[]{});
        cityList .setEntryValues(new String[]{});
        cityList .setDialogTitle(R.string.choose_city);
        cityList .setEnabled(false);


        cityList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                CharSequence[] values = cityList.getEntryValues();
                CharSequence[] entries = cityList.getEntries();
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals(newValue)) {
                        cityList.setSummary(entries[i]);
                        spHelper.setCity_name(entries[i].toString());
                    }
                }

                return true;
            }
        });


        rootScreen.addPreference(cityList);

        rating_preference = new Preference(this);
        rating_preference.setTitle("Оценить приложение");

        rating_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return false;
            }
        });


        rootScreen.addPreference(rating_preference);



        if(savedInstanceState!=null   && savedInstanceState.getSerializable(CITIES_KEY)!=null  && savedInstanceState.getSerializable(COUNTRIES_KEY)!=null ){
            isRotated = true;
            cities = (Cities)savedInstanceState.getSerializable(CITIES_KEY);
            countries = (Countries)savedInstanceState.getSerializable(COUNTRIES_KEY);
            onSucces(countries);
        }else{
            gateway.getCountries();
        }



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }


    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE,MENU_BACK_ITEM_ID, Menu.NONE, "Map")
                .setIcon(android.R.drawable.ic_menu_mapmode)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_BACK_ITEM_ID:
                if(spHelper.isSettingsExists()){
                    onBackPressed();
                }else{
                    showErrorToast();
                }
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showErrorToast(){
        if(!spHelper.isCountryExists()){

            Toast.makeText(this, R.string.choose_country_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(!spHelper.isCityExists()){
            Toast.makeText(this, R.string.choose_city_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }



    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }


    @Override
    public void onBackPressed() {
        if(spHelper.isSettingsExists()){
            super.onBackPressed();
        }else{
            showErrorToast();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CITIES_KEY, cities);
        outState.putSerializable(COUNTRIES_KEY, countries);
    }


    @Override
    public void onSucces(AbstractPOJO response) {
        if(response instanceof Countries){
            countries = (Countries)response;
            countryList.setEntries(countries.getAsStringArray());
            countryList.setEntryValues(countries.getAsIntArray());
            countryList.setEnabled(true);
            cityList.setEnabled(false);
            if(spHelper.isCountryExists()){
                gateway.getCities(spHelper.getCountry());
            }
        }
        if(response instanceof Cities){
            cities = (Cities) response;
            cityList.setEntries(cities.getAsStringArray());
            cityList.setEntryValues(cities.getAsIntArray());

            if(spHelper.isCityExists()){
                cityList.setSummary(spHelper.getCity_name());
            }else{
                cityList.setSummary(R.string.choose_city);
            }
            cityList.setEnabled(true);
        }
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
