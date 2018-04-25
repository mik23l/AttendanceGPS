package com.example.desve.attendancegps;

import android.Manifest;
import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.List;

public class JoinMeetingActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        Response.Listener<String>,
        Response.ErrorListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    int MY_LOCATION_REQUEST_CODE = 99;
    private GoogleMap mMap;

    List<MeetingInfo> meetingInfoList;
    List<View> meetingViews;
    List<Marker> markerList;

    MeetingInfo meetingInfo;

    ServerAPI serverAPI;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);


        serverAPI = new ServerAPI(this);

        linearLayout = findViewById(R.id.linear_layout);

        meetingInfoList = new ArrayList<>();
        meetingViews    = new ArrayList<>();
        markerList      = new ArrayList<>();

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
                mMap.addCircle(new CircleOptions().center(latLng).radius(100).strokeColor(Color.WHITE));
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

    private void populateLayout() {
        linearLayout.removeAllViews();
        meetingViews.clear();
        // Check for no meetings
        if (meetingInfoList.size() == 0) {
            TextView textView = new TextView(this);
            textView.setText("No Meetings to Show");
            linearLayout.addView(textView);
            return;
        }

        int i = 0;
        for (MeetingInfo meetingInfo : meetingInfoList) {
            View view = getLayoutInflater().inflate(R.layout.meeting_item, linearLayout, false);

            TextView name = view.findViewById(R.id.meeting_name);
            TextView org = view.findViewById(R.id.meeting_org);
            TextView time = view.findViewById(R.id.meeting_time);

            name.setText(meetingInfo.getName());
            org.setText(meetingInfo.getOrg());
            time.setText(meetingInfo.getStartDate());

            view.setOnClickListener(this);
            view.setTag(i);
            meetingViews.add(view);
            linearLayout.addView(view);
            i++;
        }
    }

    public void addMarkers() {
        // Iterate through list of meetings and place marker
        markerList.clear();
        int i=0;
        for (MeetingInfo meetingInfo : meetingInfoList) {
            Log.d("DEBUG", meetingInfo.getName());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(meetingInfo.getCoor())
                    .title(meetingInfo.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            marker.setTag(i);
            marker.showInfoWindow();
            i++;
            markerList.add(marker);
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
                meetingInfoList.add(m);
            }
            populateLayout();
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
        Log.d("DEBUG", "Marker Tag = " + marker.getTag());
        int idx = (int)marker.getTag();
        meetingInfo = meetingInfoList.get(idx);
        selectMeeting(idx);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    private void selectMeeting(Object tag) {
        for (int i=0; i<meetingViews.size(); i++) {
            View tempView = meetingViews.get(i);
            if(tempView.getTag() != tag) {
                LinearLayout layout = tempView.findViewById(R.id.linear_layout);
                layout.setBackground(getResources().getDrawable(R.drawable.customborder));
            }
            else {
                LinearLayout layout = tempView.findViewById(R.id.linear_layout);
                layout.setBackground(getResources().getDrawable(R.drawable.customborder2));
            }
        }
    }

    @Override
    public void onClick(View view) {
        Log.d("DEBUG", "Meeting item clicked");
        selectMeeting(view.getTag());
        int idx = (int)view.getTag();
        meetingInfo = meetingInfoList.get(idx);
        Marker marker = markerList.get(idx);
        marker.showInfoWindow();
    }

    public void onJoinMeetingClicked(View view) {
        Log.d("DEBUG", "Join meeting clicked");
        finish();
    }

    @Override
    public void onBackPressed() {
        meetingInfo = null;
        finish();
    }
}
