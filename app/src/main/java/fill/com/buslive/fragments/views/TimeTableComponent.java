package fill.com.buslive.fragments.views;

import android.content.Context;
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

import java.util.ArrayList;

import fill.com.buslive.R;
import fill.com.buslive.http.PeriodicGateway;
import fill.com.buslive.http.ServerGateway;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;

/**
 * Created by Fill on 14.10.2015.
 */
public class TimeTableComponent extends LinearLayout {

    ListView list;
    Context context;
    LayoutInflater inflater;

    ServerGateway gateway;
    PeriodicGateway periodicGateway;

    Routes routes_on_station = new Routes();

    public TimeTableComponent(Context context) {
        super(context);
    }

    public TimeTableComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_routes, this, true);
        list = (ListView) findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    public Routes getRoutes_on_station() {
        return routes_on_station;
    }

    public void setRoutes_on_station(Routes routes_on_station) {
        if (routes_on_station == null) {
            this.routes_on_station = new Routes();
        } else {
            this.routes_on_station = routes_on_station;
        }
        buildList();
    }

    private void buildList() {
        list.setAdapter(new RoutesAdapter());

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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder v_h;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_time_table, parent, false);
                v_h = new ViewHolder();
                v_h.begin_station_tv = (TextView) convertView.findViewById(R.id.begin_station_tv);
                v_h.end_station_tv = (TextView) convertView.findViewById(R.id.end_station_tv);
                v_h.bus_icon_iv = (BusNumberView) convertView.findViewById(R.id.bus_icon_iv);
                v_h.switch_compat = (SwitchCompat) convertView.findViewById(R.id.switch_compat);
                convertView.setTag(v_h);
            } else {
                v_h = (ViewHolder) convertView.getTag();
            }

            String route_name = routes_on_station.get(position).getRouteName();
            String route_number = routes_on_station.get(position).getRouteNumber();
            route_number.trim();
            route_number = route_number.replace(" ", "");

            String[] splitter_route = route_name.split("-");

            TextView begin_station_tv = v_h.begin_station_tv;
            TextView end_station_tv = v_h.end_station_tv;
            BusNumberView bus_icon_iv = v_h.bus_icon_iv;
            SwitchCompat switch_compat = v_h.switch_compat;

            if (splitter_route.length >= 2) {
                begin_station_tv.setText(splitter_route[0].trim());
                end_station_tv.setText(splitter_route[splitter_route.length-1].trim());
            } else {
                switch_compat.setText(routes_on_station.get(position).getRouteNumber() + " - " + routes_on_station.get(position).getRouteName());
            }

            //text.setCheckMarkDrawable(R.drawable.check_mark);

            bus_icon_iv.setRoute_number(route_number);



            /*if (routes_on_station.contains(routes_on_station.get(position))) {
                text.setChecked(true);
            }else{
                text.setChecked(false);
            }*/
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView begin_station_tv;
        TextView end_station_tv;
        BusNumberView bus_icon_iv;
        SwitchCompat switch_compat;
    }


}
