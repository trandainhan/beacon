package com.helios.beacon.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.adapter.BeaconListAdapter;
import com.helios.beacon.model.BeaconInfo;
import com.helios.beacon.util.Constants;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FindBeaconActivity extends Activity implements BeaconConsumer {

    protected static final String TAG = "FindBeaconActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(getApplication());

    private ListView listView;
    private List<BeaconInfo> beaconInfos = new ArrayList<BeaconInfo>();
    private BeaconListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);
        verifyBluetooth();
        beaconManager.bind(this);
        beaconManager.setForegroundScanPeriod(2000l);
        beaconManager.setForegroundBetweenScanPeriod(500l);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list);
        showTextHint();
        adapter = new BeaconListAdapter(this, beaconInfos);
        listView.setAdapter(adapter);
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }
            });
            builder.show();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)){
            beaconManager.setBackgroundMode(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)){
            beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {}

            @Override
            public void didExitRegion(Region region) {}

            @Override
            public void didDetermineStateForRegion(int state, Region region) {}
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beacons.size() > 0){
                            hideTextHint();
                            if (checkIfNumberBeaconDecreased()) return;

                            for (Beacon beacon : beacons){
                                Double dis = beacon.getDistance();
                                Log.d(TAG, dis.toString());
                                String uuid = beacon.getId1().toString();
                                String major = beacon.getId2().toString();
                                String minor = beacon.getId3().toString();
                                BeaconInfo beaconInfo = new BeaconInfo(uuid, major, minor);
                                if (!beaconInfos.contains(beaconInfo)){
                                    beaconInfos.add(beaconInfo);
                                }
                                for (BeaconInfo beaInfo: beaconInfos){
                                    if (beaInfo.equals(beaconInfo)) {
                                        beaInfo.setDistance(beacon.getDistance());
                                        break;
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // TODO:
//                            showTextHint();
                        }
                    }
                    private boolean checkIfNumberBeaconDecreased() {
                        if (beacons.size() < beaconInfos.size()){
                            beaconInfos.clear();
                            for (Beacon beacon : beacons){
                                BeaconInfo beaconInfo = new BeaconInfo();
                                beaconInfo.setUuid(beacon.getId1().toString());
                                beaconInfo.setMajor(beacon.getId2().toString());
                                beaconInfo.setMinor(beacon.getId3().toString());
                                beaconInfo.setDistance(beacon.getDistance());
                                beaconInfos.add(beaconInfo);
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                        return false;
                    }

                });
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", Identifier.parse(Constants.UUID), null, null));
        } catch (RemoteException e) {}

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse(Constants.UUID), null, null));
        } catch (RemoteException e) {}

    }

    private void showTextHint(){
        TextView textView = (TextView) findViewById(R.id.noItemsInfo);
        textView.setVisibility(View.VISIBLE);
    }

    private void hideTextHint(){
        TextView textView = (TextView) findViewById(R.id.noItemsInfo);
        textView.setVisibility(View.GONE);
    }

}
