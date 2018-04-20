package com.example.desve.attendancegps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JoinMeetingActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        Response.Listener<String>,
        Response.ErrorListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    int MY_LOCATION_REQUEST_CODE = 99;
    private GoogleMap mMap;

    Button button;

    Spinner spinner;
    List<String> spinnerArray;
    ArrayAdapter<String> adapter;

    List<LatLng> meetingCoordinates;
    HashMap<String, MeetingInfo> meetingInfoHashMap;

    ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);

        Intent intent = getIntent();

        button = findViewById(R.id.join_submit_button);

        spinner = findViewById(R.id.spinner);
        spinnerArray =  new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        serverAPI = new ServerAPI(this);
//        serverAPI.getNearybyMeetings();

        meetingCoordinates = new ArrayList<>();
        meetingInfoHashMap = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onJoinMeeting (View view) {

        String selectedMeeting = spinner.getSelectedItem().toString();

        Intent myIntent = new Intent(JoinMeetingActivity.this, JoinDetailsActivity.class);
        myIntent.putExtra("MEETING", meetingInfoHashMap.get(selectedMeeting));
        startActivity(myIntent);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

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

            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public void addMarkers() {
        // Iterate through list of meetings and place marker
        Log.d("DEBUG", "" + meetingCoordinates.size());
        for(int i = 0; i < meetingCoordinates.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(meetingCoordinates.get(i)).title(spinnerArray.get(i)));
//            mMap.addCircle(new CircleOptions().center(meetingCoordinates.get(i)).radius(200));
        }
    }

    @Override
    public void onResponse(String response) {
        try {
            Log.d("DEBUG", "MapsActivity");
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());

            JSONArray list = jsonObject.optJSONArray("nearby");

            for (int i = 0; i < list.length(); i++) {
                JSONObject meeting = list.optJSONObject(i);
                String name = meeting.getString("name");
                Double lat = meeting.getDouble("lat");
                Double lon = meeting.getDouble("lon");
                LatLng latLng = new LatLng(lat, lon);
                String id = meeting.getString("id");
                Log.d("DEBUG", "ID 1 = " + id);

                MeetingInfo m = new MeetingInfo(id, name);
                // TODO add more info
                if (meeting.has("organization")) {
                    m.setOrg(meeting.getString("organization"));
                }
                m.setOwner(meeting.getString("owner"));
                m.setStartDate(meeting.getString("date"));

                if (meeting.has("users")) {
                    List<String> users = new ArrayList<>();
                    JSONArray jsonArray = (JSONArray) meeting.get("users");
                    for (int j=0; j<jsonArray.length(); j++)
                        users.add(jsonArray.getString(j));
                    m.setUsers(users);
                    Log.d("debug", "" + meeting.getJSONArray("users"));
                }

                meetingInfoHashMap.put(name, m);
                spinnerArray.add(name);
                meetingCoordinates.add(latLng);
            }

            spinner.setAdapter(adapter);
            addMarkers();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
