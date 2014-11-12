package com.helios.beacon.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.model.BeaconInfo;

import java.util.List;

/**
 * Created by nhantran on 10/23/14.
 */
public class BeaconListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private List<BeaconInfo> data;

    public BeaconListAdapter( Activity activity, List<BeaconInfo> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_find_beacon_list_row, null);

        TextView uuid = (TextView) convertView.findViewById(R.id.uuid);
        TextView major = (TextView) convertView.findViewById(R.id.major);
        TextView minor = (TextView) convertView.findViewById(R.id.minor);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);

        BeaconInfo beaconInfo = data.get(position);

        uuid.setText(beaconInfo.getUuid());
        major.setText(beaconInfo.getMajor());
        minor.setText(beaconInfo.getMinor());
        distance.setText(beaconInfo.getDistance());

        return convertView;
    }

}
