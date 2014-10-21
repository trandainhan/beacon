package com.helios.beacon.application;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.activity.MainActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Created by nhantran on 10/17/14.
 */
public class BeaconApplication extends Application implements BootstrapNotifier, RangeNotifier {

    private static final String TAG = "BeaconApplication";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("backgroundRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        BeaconManager beaconManager =  BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundBetweenScanPeriod(10000l);

        // enables auto battery saving of about 60%
//        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.d(TAG, "did enter region.");
        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "auto launching MainActivity");

            // The very first time since boot that we detect an beacon, we launch the
            // MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
            // to keep multiple copies of this activity from getting created if the user has
            // already manually launched the app.
            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            // If we have already seen beacons and launched the MainActivity before, we simply
            // send a notification to the user on subsequent detections.
            Log.d(TAG, "Sending notification.");
            sendNotification();
        }

    }

    @Override
    public void didExitRegion(Region arg0) {
        // Don't care
        Log.d(TAG, "Did exit region");
    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Beacon Reference Application")
                        .setContentText("An beacon is nearby.")
                        .setSmallIcon(R.drawable.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG, "in Range region");
    }
}