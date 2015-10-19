package fill.com.buslive.fragments.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fill.com.buslive.R;
import fill.com.buslive.http.pojo.Routes;
import fill.com.buslive.utils.L;

/**
 * Created by Fill on 31.12.2014.
 * <p/>
 * Компонент отображающий список доступных маршрутов автобусов.
 */


public class RoutesComponent extends LinearLayout {

    ListView list;
    Context context;
    LayoutInflater inflater;
    Routes routes;
    OnCheckRouteListener onCheckRouteListener;

    Routes checkedset = new Routes();

    ExecutorService service = Executors.newFixedThreadPool(1);

    public RoutesComponent(Context context) {
        super(context);
    }

    public RoutesComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_routes, this, true);
        list = (ListView) findViewById(R.id.list);
        //list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //setListeners();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("628FEC8F638678EB52585E50B797BD15")
                .build();
        mAdView.loadAd(adRequest);
    }

    public Routes getCheckedSet() {
        return checkedset;
    }

    public void setCheckedset(Routes checkedset) {
        if (checkedset == null) {
            this.checkedset = new Routes();
        } else {
            this.checkedset = checkedset;
        }
    }

    private void setListeners() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SwitchCompat switchCompat = ((SwitchCompat) view.findViewById(R.id.switch_compat));
                if (switchCompat.isChecked()) {
                    switchCompat.setChecked(false);
                    checkedset.getRoutes().remove(routes.get(position));
                } else {
                    switchCompat.setChecked(true);
                    checkedset.addRoute(routes.get(position));
                }
                if (onCheckRouteListener != null) {
                    onCheckRouteListener.onCheckRoute(checkedset);
                }
            }
        });
    }

    public void setOnCheckRouteListener(OnCheckRouteListener onCheckRouteListener) {
        this.onCheckRouteListener = onCheckRouteListener;
    }


    public void setRoutes(Routes routes) {
        this.routes = routes;
        buildList();
    }


    private void buildList() {
        list.setAdapter(new RoutesAdapter());
    }

    public class RoutesAdapter extends BaseAdapter {

        public RoutesAdapter() {

        }

        @Override
        public int getCount() {
            return routes.size();
        }

        @Override
        public Object getItem(int position) {
            return routes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder v_h;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_route, parent, false);
                v_h = new ViewHolder();
                v_h.begin_station_tv = (TextView) convertView.findViewById(R.id.begin_station_tv);
                v_h.end_station_tv = (TextView) convertView.findViewById(R.id.end_station_tv);
                v_h.bus_icon_iv = (BusNumberView) convertView.findViewById(R.id.bus_icon_iv);
                v_h.switch_compat = (SwitchCompat) convertView.findViewById(R.id.switch_compat);
                convertView.setTag(v_h);
            } else {
                v_h = (ViewHolder) convertView.getTag();
            }

            String route_name = routes.get(position).getRouteName();
            String route_number = routes.get(position).getRouteNumber();
            route_number.trim();
            route_number = route_number.replace(" ", "");

            String[] splitter_route = route_name.split("-");

            TextView begin_station_tv = v_h.begin_station_tv;
            TextView end_station_tv = v_h.end_station_tv;
            BusNumberView bus_icon_iv = v_h.bus_icon_iv;
            final SwitchCompat switch_compat = v_h.switch_compat;

            if (splitter_route.length >= 2) {
                begin_station_tv.setText(splitter_route[0].trim());
                end_station_tv.setText(splitter_route[splitter_route.length - 1].trim());
            } else {
                switch_compat.setText(routes.get(position).getRouteNumber() + " - " + routes.get(position).getRouteName());
            }

            bus_icon_iv.setRoute_number(route_number);

            if (checkedset.getRoutes().contains(routes.get(position))) {
                switch_compat.setChecked(true);
            }else{
                switch_compat.setChecked(false);
            }

            switch_compat.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switch_compat.isChecked()) {
                        checkedset.addRoute(routes.get(position));
                    } else {
                        checkedset.getRoutes().remove(routes.get(position));
                    }
                    if (onCheckRouteListener != null) {
                        onCheckRouteListener.onCheckRoute(checkedset);
                    }
                }
            });


            return convertView;
        }
    }


    public static class ViewHolder {
        TextView begin_station_tv;
        TextView end_station_tv;
        BusNumberView bus_icon_iv;
        SwitchCompat switch_compat;
    }


    public interface OnCheckRouteListener {
        void onCheckRoute(Routes checkedRoutes);
    }



}
