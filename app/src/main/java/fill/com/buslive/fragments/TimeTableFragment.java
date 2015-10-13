package fill.com.buslive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fill.com.buslive.R;
import fill.com.buslive.fragments.views.TimeTableComponent;

/**
 * Created by Fill on 14.10.2015.
 */
public class TimeTableFragment extends Fragment {

    TimeTableComponent time_table_component;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(time_table_component==null){
            time_table_component = ((TimeTableComponent) LayoutInflater.from(getContext()).inflate(R.layout.fragment_time_table, container, false));
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
