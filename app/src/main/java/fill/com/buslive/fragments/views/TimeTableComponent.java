package fill.com.buslive.fragments.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fill.com.buslive.R;
import fill.com.buslive.http.PeriodicGateway;
import fill.com.buslive.http.ResponseCallback;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.http.pojo.Predictions;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;
import fill.com.buslive.utils.SPHelper;

/**
 * Created by Fill on 14.10.2015.
 */
public class TimeTableComponent extends LinearLayout implements ResponseCallback {

    ListView list;
    Context context;
    LayoutInflater inflater;

    PeriodicGateway periodicGateway;

    Routes routes_on_station = new Routes();
    Routes checked_routes = new Routes();
    String currentStation_id;

    SPHelper spHelper;

    RoutesAdapter adapter;

    Predictions predictions = new Predictions();

    OnCheckRouteListener onCheckRouteListener;


    public TimeTableComponent(Context context) {
        super(context);
    }

    public TimeTableComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        spHelper = SPHelper.getInstance(context);
        setOrientation(LinearLayout.VERTICAL);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_time_table, this, true);
        list = (ListView) findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        periodicGateway = new PeriodicGateway(context, this);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("628FEC8F638678EB52585E50B797BD15")
                .build();
        mAdView.loadAd(adRequest);
    }

    public Routes getRoutes_on_station() {
        return routes_on_station;
    }

    public void setRoutes_on_station(Routes routes_on_station, Routes checked_routes, String currentStation_id) {
        if (routes_on_station == null) {
            this.routes_on_station = new Routes();
        } else {
            this.routes_on_station = routes_on_station;
        }

        this.checked_routes = checked_routes;
        this.currentStation_id = currentStation_id;

        buildList();

        periodicGateway.startGetPredictions(spHelper.getCity(), currentStation_id);

    }

    private void buildList() {
        adapter = new RoutesAdapter();
        list.setAdapter(adapter);
    }


    public void setOnCheckRouteListener(OnCheckRouteListener onCheckRouteListener) {
        this.onCheckRouteListener = onCheckRouteListener;
    }

    public class RoutesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return routes_on_station.size();
        }

        @Override
        public Object getItem(int position) {
            return routes_on_station.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder v_h;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_time_table, parent, false);
                v_h = new ViewHolder();
                v_h.prediction_tv = (TextView) convertView.findViewById(R.id.prediction_tv);
                v_h.bus_icon_iv = (BusNumberView) convertView.findViewById(R.id.bus_icon_iv);
                v_h.switch_compat = (SwitchCompat) convertView.findViewById(R.id.switch_compat);
                convertView.setTag(v_h);
            } else {
                v_h = (ViewHolder) convertView.getTag();
            }
            String route_number = routes_on_station.get(position).getRouteNumber();
            route_number.trim();
            route_number = route_number.replace(" ", "");


            BusNumberView bus_icon_iv = v_h.bus_icon_iv;
            final SwitchCompat switch_compat = v_h.switch_compat;

            bus_icon_iv.setRoute_number(route_number);


            TextView prediction_tv = v_h.prediction_tv;
            String busResportRouteId = routes_on_station.getRoutes().get(position).getBusreportRouteId();


            for(Predictions.Prediction prediction : predictions.getPredictions()){
                 if(prediction.getRouteId().equals(busResportRouteId)){
                     int prediction_time = Integer.valueOf(prediction.getPrediction());
                     int minute = (int)Math.floor(prediction_time / 60);

                     if(prediction_time>15*60){
                         prediction_tv.setText("--:--");
                         prediction_tv.setTextColor(Color.parseColor("#c6c6c6"));
                     }

                     if(prediction_time<=15*60){
                         prediction_tv.setText(minute+" мин.");
                         prediction_tv.setTextColor(Color.parseColor("#41b613"));
                     }
                     if(prediction_time<=5*60){
                         prediction_tv.setText(minute+" мин.");
                         prediction_tv.setTextColor(Color.parseColor("#eac81e"));
                     }
                     if(prediction_time<=1*60){
                         prediction_tv.setText(prediction_time+" сек.");
                         prediction_tv.setTextColor(Color.parseColor("#ea1e1e"));
                     }

                 }else{
                     prediction_tv.setText("--:--");
                     prediction_tv.setTextColor(Color.parseColor("#c6c6c6"));
                 }
            }


            if (checked_routes.getRoutes().contains(routes_on_station.get(position))) {
                switch_compat.setChecked(true);
            }else{
                switch_compat.setChecked(false);
            }

            switch_compat.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switch_compat.isChecked()) {
                        checked_routes.addRoute(routes_on_station.get(position));
                    } else {
                        checked_routes.getRoutes().remove(routes_on_station.get(position));
                    }
                    if (onCheckRouteListener != null) {
                        onCheckRouteListener.onCheckRoute(checked_routes);
                    }
                }
            });


            return convertView;
        }
    }

    public static class ViewHolder {
        TextView prediction_tv;
        BusNumberView bus_icon_iv;
        SwitchCompat switch_compat;
    }



    @Override
    public void onSucces(AbstractPOJO response) {
        predictions = (Predictions)response;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message) {

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        periodicGateway.pauseGetPredictions();
        periodicGateway = null;
    }


    public interface OnCheckRouteListener {
        void onCheckRoute(Routes checkedRoutes);
    }
}
