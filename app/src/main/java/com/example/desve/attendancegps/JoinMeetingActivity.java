package com.example.desve.attendancegps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class JoinMeetingActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        Response.Listener<String>,
        Response.ErrorListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    int MY_LOCATION_REQUEST_CODE = 99;
    private GoogleMap mMap;

    HashMap<String, MeetingInfo> meetingInfoHashMap;
    MeetingInfo meetingInfo;

    ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);


        serverAPI = new ServerAPI(this);

        meetingInfoHashMap = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        Log.d("DEBUG", "onMapReady called...");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addCircle(new CircleOptions().center(latLng).radius(200).strokeColor(Color.WHITE));
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
                // Set a listener for info window events.
                mMap.setOnInfoWindowClickListener(this);

                serverAPI.getNearbyMeetings(latLng);

            }
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION }, MY_LOCATION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG", "Approved");
            } else {
                // Permission was denied. Display an error message.
                Log.d("DEBUG", "Denied");
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    public void addMarkers() {

        // Check for no meetings
        if (meetingInfoHashMap.size() == 0) {

            new android.app.AlertDialog.Builder(this)
                    .setTitle("No meetings currently available!")
                    .setNeutralButton(android.R.string.ok, null).show();

        }

        // Iterate through list of meetings and place marker
        for (MeetingInfo meetingInfo : meetingInfoHashMap.values()) {
            Log.d("DEBUG", meetingInfo.getName());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(meetingInfo.getCoor())
                    .title(meetingInfo.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

            marker.showInfoWindow();
        }

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray list = jsonObject.optJSONArray("nearby");

            for (int i = 0; i < list.length(); i++) {
                JSONObject meeting = list.optJSONObject(i);
                MeetingInfo m = new MeetingInfo(meeting);
                meetingInfoHashMap.put(m.getName(), m);
            }

            addMarkers();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void finish() {
        Log.d("DEBUG", "Finishing join activity");
        Intent intent = new Intent();
        intent.putExtra("MEETING", meetingInfo);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final String title = marker.getTitle();
        new android.app.AlertDialog.Builder(this)
                .setTitle(marker.getTitle())
                .setMessage("Join this meeting?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(JoinMeetingActivity.this, "Joining Meeting", Toast.LENGTH_SHORT).show();
                        meetingInfo = meetingInfoHashMap.get(title);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
