package fill.com.buslive.fragments.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

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

    ArrayList<Routes.Route> checkedset = new ArrayList<>();

    public RoutesComponent(Context context) {
        super(context);
    }

    public RoutesComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_routes, this, true);
        list = (ListView) v.findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setListeners();
    }

    public ArrayList<Routes.Route> getCheckedSet() {
        return checkedset;
    }

    public void setCheckedset(ArrayList<Routes.Route> checkedset) {
        if (checkedset == null) {
            this.checkedset = new ArrayList<>();
        } else {
            this.checkedset = checkedset;
        }
    }

    private void setListeners() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView textView = ((CheckedTextView) view.findViewById(android.R.id.text1));
                if (textView.isChecked()) {
                    textView.setChecked(false);
                    checkedset.remove(routes.get(position));
                } else {
                    textView.setChecked(true);
                    checkedset.add(routes.get(position));
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


        Bitmap bus_icon_bitmap;
        Bitmap bm;
        Canvas canv;
        Bitmap scalable;

        ArrayList<Bitmap> createdBitmap = new ArrayList<>();


        public RoutesAdapter() {

            BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.bus_number_icon);
            Bitmap bitmap = drawable.getBitmap();

            this.bus_icon_bitmap = bitmap;
            scalable = Bitmap.createScaledBitmap(bus_icon_bitmap, 200, 100, false);
            //canv.drawBitmap(scalable, 0, 0, new Paint());
            createdBitmap = new ArrayList<>(routes.size());

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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder v_h;
            boolean isCreated;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_route, parent, false);
                v_h = new ViewHolder();
                v_h.begin_station_tv = (TextView) convertView.findViewById(R.id.begin_station_tv);
                v_h.end_station_tv = (TextView) convertView.findViewById(R.id.end_station_tv);
                v_h.bus_icon_iv = (ImageView) convertView.findViewById(R.id.bus_icon_iv);
                v_h.text = (CheckedTextView) convertView.findViewById(android.R.id.text1);
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
            ImageView bus_icon_iv = v_h.bus_icon_iv;
            CheckedTextView text = v_h.text;

            if (splitter_route.length >= 2) {
                begin_station_tv.setText(splitter_route[0].trim());
                end_station_tv.setText(splitter_route[splitter_route.length-1].trim());
            } else {
                text.setText(routes.get(position).getRouteNumber() + " - " + routes.get(position).getRouteName());
            }

            text.setCheckMarkDrawable(R.drawable.check_mark);

            bm = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);
            canv = new Canvas(bm);
            canv.drawBitmap(scalable, 0, 0, new Paint());
            /**
             * Ширина текста 1/2 от базовой
             */
            float textSize = new Float(bm.getWidth()/2.2);
            if(route_number.length()>3){
                textSize /= 1.3;
            }

            Paint textPaint = new Paint();
            textPaint.setTextSize(textSize);
            textPaint.setAntiAlias(true);
            textPaint.setFilterBitmap(true);

            Rect bounds = new Rect();
            textPaint.getTextBounds(route_number, 0, route_number.length(), bounds);
            int x = (canv.getWidth() / 2) - (bounds.width() / 2) - (route_name.length() / 2);
            float y = (canv.getHeight() / 2) - (bounds.height() / 2) - textPaint.descent() - textPaint.ascent();
            canv.drawText(route_number, x, y, textPaint);

            Paint stkPaint = new Paint();
            stkPaint.setStyle(Paint.Style.STROKE);
            stkPaint.setTextSize(textSize);
            stkPaint.setStrokeWidth(3);
            stkPaint.setColor(Color.WHITE);
            stkPaint.setAntiAlias(true);
            stkPaint.setFilterBitmap(true);

            canv.drawText(route_number, x, y, stkPaint);

            bus_icon_iv.setImageBitmap(bm);

            if (checkedset.contains(routes.get(position))) {
                text.setChecked(true);
            }
            return convertView;
        }
    }


    public static class ViewHolder {
        TextView begin_station_tv;
        TextView end_station_tv;
        ImageView bus_icon_iv;
        CheckedTextView text;
    }


    public interface OnCheckRouteListener {
        void onCheckRoute(ArrayList<Routes.Route> checkedRoutes);
    }

}
