package fill.com.buslive.fragments.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import fill.com.buslive.R;

/**
 * Created by Fill on 24.12.2014.
 */
public class Drawer extends LinearLayout {

    ListView list;
    Context context;

    public static final int MENU_ROUTE = 0;
    public static final int MENU_STATION = 1;
    public static final int MENU_EXIT = 2;

    DrawerClickListener mClickListener;


    public Drawer(Context context) {
        super(context);
    }

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.drawer_view, this, true);
        list = (ListView)v.findViewById(R.id.list);
        buildList();
    }


    private void buildList(){
        list.setAdapter(new MenuAdapter());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case MENU_ROUTE:
                        menuClick(MENU_ROUTE);
                        break;

                    case MENU_STATION:
                        menuClick(MENU_STATION);
                        break;

                    case MENU_EXIT:
                        menuClick(MENU_EXIT);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setOnDrawerClickListener(DrawerClickListener mClickListener){
        this.mClickListener = mClickListener;
    }


    private void menuClick(int id){
        if(mClickListener!=null){
            mClickListener.onDrawerClick(id);
        }
    }




    public interface DrawerClickListener{
        public void onDrawerClick(int id);
    }

    private class MenuAdapter extends BaseAdapter{

        ArrayList<Integer> arrayList = new ArrayList<>();

        private MenuAdapter() {
            arrayList.add(R.drawable.ic_menu_route);
            arrayList.add(R.drawable.ic_menu_station);
            arrayList.add(R.drawable.ic_menu_exit);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(arrayList.get(position));
            imageView.setPadding(0,5,0,5);
            return imageView;
        }
    }


}
