package com.helios.beacon.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.adapter.BeaconListAdapter;
import com.helios.beacon.model.BeaconInfo;
import com.helios.beacon.util.Constants;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
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
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

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

        Log.d(TAG, "Unbind");
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Enter beacon region", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void didExitRegion(Region region) {}

            @Override
            public void didDetermineStateForRegion(int state, Region region) {}
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideTextHint();
                            if (checkIfNumberBeaconDecreased()) return;

                            for (Beacon beacon : beacons){
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
                } else {
                    showTextHint();
                }
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
