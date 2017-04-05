package com.example.satku.xfusion_platformapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.teramatrix.xfusionlibrary.IoTSDK;
import com.teramatrix.xfusionlibrary.receivers.LocationUpdateReceiver;

import java.util.Formatter;

import fragment.DashboardFragment;
import fragment.DeviceMapFragment;
import fragment.LogoutFragment;
import fragment.SettingFragment;

import static com.example.satku.xfusion_platformapp.R.id.drawer;
import static com.example.satku.xfusion_platformapp.R.id.logout;

public class MainActivity extends AppCompatActivity implements LocationUpdateReceiver.INotifyLocationUpdates ,FragmentManager.OnBackStackChangedListener {
    LocationUpdateReceiver locationUpdateReceiver;
    private Menu mymenu;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private String text;
    public static final String STACK = "Main";
    private FragmentManager fragmentManager;
    private Formatter formatter;
    private Fragment fragment;
    public static android.app.AlertDialog dialog;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        Util.loadFragment(MainActivity.this, new DashboardFragment());


    }
    private void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        fragmentManager=getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                switch (id) {
                    case R.id.dashboard:
                        Toast.makeText(getApplicationContext(), "DASHBOARD", Toast.LENGTH_SHORT).show();
                        Util.loadFragment(MainActivity.this,new DashboardFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "SETTINGS", Toast.LENGTH_SHORT).show();
                        Util.loadFragment(MainActivity.this, new SettingFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.devicemap:
                        Toast.makeText(getApplicationContext(), "DEVICE MAP", Toast.LENGTH_SHORT).show();
                        Util.loadFragment(MainActivity.this, new DeviceMapFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case logout:
                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        Util.loadFragment(MainActivity.this, new LogoutFragment());
                        alertDialog.setTitle("Warning");
                        alertDialog.setMessage("This action will logout you from the application ." +
                                "Do you really want to logout?");

                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show();
                                finish();


                            }

                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface dialog, int which) {

                                                 }

                        });
                        alertDialog.show();
                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView txt_email = (TextView) header.findViewById(R.id.txt_email);
        txt_email.setText("preeti.burad@teramatrix.co");
        drawerLayout = (DrawerLayout) findViewById(drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

/*
    @Override
    protected void onStart() {
        super.onStart();
//Initialize IoT Sdk Servcies
        IoTSDK ioTSdk = IoTSDK.getInstance(MainActivity.this);
//Configure Location Tracking
        ioTSdk.setLocationTrackingEnabled(true);
        ioTSdk.setLocationUpdateInterval(20);
        ioTSdk.setLocationTrackingServiceExecuteMode(IoTSDK.SERVICE_EXECUTE_MODE_ALWAYS);
//Configure device|app servcie data API
        ioTSdk.setDeviceParametersToTrack(IoTSDK.DEVICE_PARAMETERS_PHONE,
                IoTSDK.DEVICE_PARAMETERS_NETWORKS, IoTSDK.DEVICE_PARAMETERS_TRAFIC);
        ioTSdk.setDevceiDataAPIUpdateInterval(30);
        try {
            ioTSdk.initService();
        } catch (UnAuthorizedAccess unAuthorizedAccess) {
            unAuthorizedAccess.printStackTrace();
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        locationUpdateReceiver = IoTSDK.registerLocationUpdateReeiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IoTSDK.unregisterLocationUpdateReeiver(this, locationUpdateReceiver);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentById(R.id.fragment_container) instanceof DashboardFragment) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("no", null).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), " location ",
                Toast.LENGTH_SHORT).show();
    }


    public void resetUpdating() {
        // Get our refresh item from the menu
        MenuItem m = mymenu.findItem(R.id.action_refresh);
        if (m.getActionView() != null) {
            // Remove the animation.
            m.getActionView().clearAnimation();
            m.setActionView(null);
        }
    }



    @Override
    public void onBackStackChanged() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof DashboardFragment) {
            navigationView.setCheckedItem(R.id.dashboard);
        } else if (fragment instanceof SettingFragment) {
            navigationView.setCheckedItem(R.id.settings);
        } else if (fragment instanceof DeviceMapFragment) {
            navigationView.setCheckedItem(R.id.devicemap);
        }

    }

}

