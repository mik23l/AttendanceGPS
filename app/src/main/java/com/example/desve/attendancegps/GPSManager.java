package com.example.desve.attendancegps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Samuel McGhee on 4/18/2018.
 */

public class GPSManager implements LocationListener {
    LocationManager locationManager;
    String LOCATIONPROVIDER = LocationManager.GPS_PROVIDER;
    Activity parentActivity;

    public GPSManager(Activity ma) {
        this.parentActivity= ma;
        locationManager = (LocationManager) parentActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void register() {
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                ) {
            locationManager.requestLocationUpdates(LOCATIONPROVIDER, 1000, 1, this);
            currentLocation = locationManager.getLastKnownLocation(LOCATIONPROVIDER);
//            mapsActivity.updateCurrentLocation(currentLocation);
        }
    }

    public void unregister() {
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                ) {
            locationManager.removeUpdates(this);
        }
    }

    Location currentLocation;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
//        mapsActivity.updateCurrentLocation(currentLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}