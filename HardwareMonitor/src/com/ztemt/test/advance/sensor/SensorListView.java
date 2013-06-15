package com.ztemt.test.advance.sensor;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ztemt.test.advance.R;

public class SensorListView extends LinearLayout {

    public SensorListView(Context context) {
        super(context);

        SensorManager sensorMgr = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.main, null);

        LinearLayout viewList = (LinearLayout) scrollView.getChildAt(0);
        List<SensorListItem> sensorList = SensorListItem.getSensorList().size() == 0 ? SensorListItem
                .createSensorList(sensorMgr.getSensorList(Sensor.TYPE_ALL))
                : SensorListItem.getSensorList();

        int i = 0;
        for (SensorListItem sensorItem : sensorList) {
            SensorListItemView itemView = new SensorListItemView(context, sensorItem);
            viewList.addView(itemView, i);
            SensorClickListener.updateListItemView(sensorItem, itemView);
            i++;
        }

        addView(scrollView);
    }
}
