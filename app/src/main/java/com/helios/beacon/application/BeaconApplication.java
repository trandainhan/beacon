package com.helios.beacon.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.helios.beacon.activity.MainActivity;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.Region;

/**
 * Created by nhantran on 10/17/14.
 */
public class BeaconApplication extends Application implements BootstrapNotifier {

    private static final String TAG = "BeaconApplication";
    private RegionBootstrap regionBootstrap;

    private BackgroundPowerSaver backgroundPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        regionBootstrap.disable();
        Intent intent = new Intent(this, MainActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region arg0) {
        // Don't care
    }
}