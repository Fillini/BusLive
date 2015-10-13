package fill.com.buslive.fragments.views;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import fill.com.buslive.R;

/**
 * Created by Fill on 14.10.2015.
 */
public class TimeTableComponent extends LinearLayout {

    ListView list;
    Context context;
    LayoutInflater inflater;

    public TimeTableComponent(Context context) {
        super(context);
    }

    public TimeTableComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_routes, this, true);
        list = (ListView) v.findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
