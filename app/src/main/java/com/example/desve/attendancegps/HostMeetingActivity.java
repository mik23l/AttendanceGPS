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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class HostMeetingActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, Response.Listener<String>, Response.ErrorListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    static final int DETAILS_CODE = 1;
    int MY_LOCATION_REQUEST_CODE = 99;
    private GoogleMap mMap;

    Button button;
    EditText editText;
    EditText organizationEditText;

    ServerAPI serverAPI;

    DatabaseManager databaseManager;
    UserInfo userInfo;
    MeetingInfo meetingInfo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_meeting);

        button = findViewById(R.id.host_submit_button);
        editText = findViewById(R.id.host_meeting_edit_text);
        organizationEditText = findViewById(R.id.organization_edit_text);

        serverAPI = new ServerAPI(this);

        databaseManager = new DatabaseManager(this);

        databaseManager.open();
        userInfo = databaseManager.getUserFromDB();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onHostMeeting (View view) {
        Location currentLocation = mMap.getMyLocation();
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.d("DEBUG", "Start meeting called...");
            serverAPI.startMeeting(editText.getText().toString(),
                    latLng, userInfo.m_id,
                    organizationEditText.getText().toString());
        }
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

            }
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == DETAILS_CODE) {
//            Log.d("DEBUG", "Ending host meeting actvity");
//            finish();
//        }
//    }


    @Override
    public void finish() {
        Log.d("DEBUG", "Finishing host activity");
        Intent intent = new Intent();
        intent.putExtra("MEETING", meetingInfo);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onResponse(String response) {
        try {
            Log.d("DEBUG", "HostMeetingActivity");
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());

            if(jsonObject.has("ERROR")) {
                Toast.makeText(getApplicationContext(), "Already own an active meeting. End your previous meeting to start another!", Toast.LENGTH_LONG).show();
            }
            else {
                meetingInfo = new MeetingInfo(jsonObject);
//                Intent myIntent = new Intent(HostMeetingActivity.this, HostDetailsActivity.class);
//                myIntent.putExtra("MEETING", meetingInfo);
//                HostMeetingActivity.this.startActivity(myIntent);
//                startActivityForResult(myIntent, DETAILS_CODE);
                Log.d("DEBUG", "calling finish host");
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("DEBUG",error.toString());
    }
}
