package com.helios.beacon.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.adapter.TabsPagerAdapter;
import com.helios.beacon.fragment.BaseFragment;
import com.helios.beacon.fragment.OrderFragment;
import com.helios.beacon.util.Constants;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener, BeaconConsumer {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Paypal setup
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AchJpRAMiezOe_UZSPexSrcJow2MqBTpwAKzDMT_mZLdVXNaIeAx888aFRPa";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private Menu menu;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(getApplication());

    private String major = "1";
    private String minor = "226";
    private String[] tabs = {"Order"};

    private boolean isDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpBeaconService();

        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);
                BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + i);
                fragment.onCreateOptionsMenu(menu, getMenuInflater());
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        startPaypalService();

    }

    private void startPaypalService() {
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void setUpBeaconService() {
        beaconManager.bind(this);
        beaconManager.setForegroundScanPeriod(1000l);
        beaconManager.setForegroundBetweenScanPeriod(1000l);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        stopService(new Intent(this, PayPalService.class));
    }

    private void sendOrderConfirmReq(){
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_find_beacon) {
            Intent intent = new Intent(this, FindBeaconActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beacons.size() > 0) {
                            if (isDetected == false){
                                BaseFragment fragment = (OrderFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
                                fragment.makeRequestData(fragment.getView(), major, minor);
                            }
                            isDetected = true;

                        }
                    }
                });
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse(Constants.UUID), null, null));
        } catch (RemoteException e) {}

    }

    public void setMajorMinor(String major, String minor){
        this.major = "1";
        this.minor = "226";
    }

    public String getMajor(){
        return this.major;
    }
    public String getMinor(){
        return this.minor;
    }
}
