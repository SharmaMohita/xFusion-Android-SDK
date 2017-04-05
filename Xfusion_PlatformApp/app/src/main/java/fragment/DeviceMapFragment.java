package fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.satku.xfusion_platformapp.MyLocation;
import com.example.satku.xfusion_platformapp.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;

import in.teramatrix.utilities.model.Distance;
import in.teramatrix.utilities.service.DistanceCalculator;
import in.teramatrix.utilities.service.LocationHandler;
import in.teramatrix.utilities.service.RouteDesigner;
import in.teramatrix.utilities.util.MapUtils;

import static in.teramatrix.utilities.service.LocationHandler.Filters.ACCURACY;
import static in.teramatrix.utilities.service.LocationHandler.Filters.DISTANCE;
import static in.teramatrix.utilities.service.LocationHandler.Filters.NULL;
import static in.teramatrix.utilities.service.LocationHandler.Filters.RADIUS;
import static in.teramatrix.utilities.service.LocationHandler.Filters.SPEED;
import static in.teramatrix.utilities.service.LocationHandler.Filters.ZERO;

/**
 * Created by mohita on 2/13/2017.
 */

public class DeviceMapFragment extends Fragment implements OnMapReadyCallback, LocationListener, DatePickerDialog.OnDateSetListener {
    private View rootView;
    private GoogleMap mMap;
    private Marker marker;
    private LocationHandler locationHandler;
    private FloatingActionButton btnFab;
    boolean isFABOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//plotMarkers();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);

            FragmentManager manager = getChildFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

            btnFab = (FloatingActionButton) rootView.findViewById(R.id.btnFloatingAction);
            btnFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    update();
                    if (!isFABOpen) {
                        showFABMenu();
                    }
                }
            });

        }
        return rootView;

    }

    private void showFABMenu() {
        isFABOpen = true;
        btnFab.setVisibility(View.VISIBLE);
        btnFab.animate().rotationBy(180);


    }

    private void update() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        /*new DatePickerDialog(this,d,dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();*/

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Log.e("DeviceMapFragment", "GOT IT");
        Calendar from = Calendar.getInstance();
        from.set(Calendar.YEAR, year);
        from.set(Calendar.MONTH, monthOfYear);
        from.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Calendar to = Calendar.getInstance();
        to.set(Calendar.YEAR, yearEnd);
        to.set(Calendar.MONTH, monthOfYearEnd);
        to.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);

        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED);
        Log.e("DeviceMapFrag", from.getTimeInMillis() + "-" + to.getTimeInMillis());
        Cursor cursor = ActiveAndroid.getDatabase().rawQuery("select * from MyLocation where logTime between " + from.getTimeInMillis() + " and  " + to.getTimeInMillis(), null);
        if (cursor.moveToFirst()) {
            do {
                LatLng latlng = new LatLng(cursor.getDouble(1), cursor.getDouble(3));
                polylineOptions.add(latlng);
            } while (cursor.moveToNext());
        }
       /* List<MyLocation> list = new Select().from(MyLocation.class)
                *//*.where("logTime between " + to.getTimeInMillis() + " and " + from.getTimeInMillis())*//*.execute();


        *//*List<MyLocation> list = new Select().from(MyLocation.class)
                .where("logTime >= " + from.getTimeInMillis() + " and logTime <= " + to.getTimeInMillis()).execute();*//*
        //ActiveAndroid.getDatabase().rawQuery("SELECT * from MyLocation where", null);
        for (MyLocation location : list)
            Log.e("DeviceMapFrag", location.getLatitude() + ", " + location.getLongitude());*/


        // Create polyline options with existing LatLng ArrayList


        // Adding multiple points in map using polyline and arraylist
        if (mMap != null) mMap.addPolyline(polylineOptions);


    }


    @Override
    public void onMapReady(GoogleMap mMap) {

        this.mMap = mMap;

      //  LatLng jaipur = new LatLng(-34, 151);
    //    mMap.addMarker(new MarkerOptions().position(jaipur).title("Jaipur"));
   //     mMap.moveCamera(CameraUpdateFactory.newLatLng(jaipur));
        // Add a marker in Sydney and move the camera
        int interval = 1000 * 10;
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

        this.locationHandler = new LocationHandler(getActivity())
                .setInterval(interval)                                    // 10 Seconds
                .setFastestInterval(interval)                             // 10 Seconds
                .setPriority(priority)
                .setFilters(NULL, ZERO, ACCURACY, SPEED, RADIUS, DISTANCE)
                .setLocationListener(this)
                .start();
        /* new RouteDesigner(getActivity(), mMap)
                .setOrigin(new LatLng(26.926106, 75.792809))
                .setDestination(new LatLng(26.449743, 74.704028))
                .design();*/
    }
         @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (marker == null) {
                marker = MapUtils.addMarker(mMap, latLng);
                MapUtils.animateCamera(mMap, latLng, 12);
            } else {
                marker.setPosition(latLng);
            }
            new MyLocation(location.getLatitude(), location.getLongitude(), location.getTime()).save();

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationHandler != null) {
            locationHandler.stop();
        }
    }
   /* private void plotMarkers() {
        try {
            mMap.addMarker(new MarkerOptions()
                    .title("Source")
                    .position(Source)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_source)));

            mMap.addMarker(new MarkerOptions()
                    .title("Destination")
                    .position(Destination)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_destination)));
        }
        catch (NullPointerException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }*/
    }
