package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JoinDetailsActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener, SwipeRefreshLayout.OnRefreshListener {

    TextView meetingName;
    TextView orgText;
    TextView ownerText;
    TextView startText;
    MeetingInfo meetingInfo;
    ServerAPI serverAPI;
    LinearLayout userLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_details);

        serverAPI = new ServerAPI(this);

        databaseManager = new DatabaseManager(this);

        // Check if user already exists in the database and attempt to sign in
        databaseManager.open();
        UserInfo userInfo = databaseManager.getUserFromDB();

        meetingName = findViewById(R.id.meeting_name);
        orgText = findViewById(R.id.organization);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        startText = findViewById(R.id.start_time);
        ownerText = findViewById(R.id.owner_name);
        userLayout = findViewById(R.id.users_list);

        swipeRefreshLayout.setOnRefreshListener(this);

        Intent intent = getIntent();
        meetingInfo = (MeetingInfo) intent.getSerializableExtra("MEETING");

        serverAPI.joinMeeting(String.valueOf(meetingInfo.getId()), userInfo.m_username);

        meetingName.setText(meetingInfo.getName());
        updateUI();
    }

    private void updateUI() {
        if (meetingInfo.getOrg() != null) {
            orgText.setText("Organization: " + meetingInfo.getOrg());
        }
        else {
            orgText.setText("");
        }

        if (meetingInfo.getOwner() != null) {
            ownerText.setText("Owner: " + meetingInfo.getOwner());
        }
        else {
            ownerText.setText("");
        }

        if (meetingInfo.getStartDate() != null) {
            startText.setText("Start Time: " + meetingInfo.getStartDate());
        }
        else {
            startText.setText("");
        }

        if (meetingInfo.getUsers() != null) {
            userLayout.removeAllViews();
            for (String user : meetingInfo.getUsers()) {
                TextView tv = new TextView(this);
                tv.setText(user);
                userLayout.addView(tv);
            }
        }
    }

    private void handleRefresh(JSONObject object) {
        Log.d("DEBUG", "handle refresh");
        parseJSON(object);
        updateUI();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void parseJSON(JSONObject object) {
        try {
            String name = object.getString("name");
            Double lat = object.getDouble("lat");
            Double lon = object.getDouble("lon");
            LatLng latLng = new LatLng(lat, lon);
            String id = object.getString("id");
            String start_time = object.getString("date");

            meetingInfo.setOwner(object.getString("owner"));
            meetingInfo.setStartDate(start_time);
            // TODO add more info
            if (object.has("organization")) {
                meetingInfo.setOrg(object.getString("organization"));
            }


            if (object.has("users")) {
                List<String> users = new ArrayList<>();
                JSONArray jsonArray = (JSONArray) object.get("users");
                for (int j=0; j<jsonArray.length(); j++)
                    users.add(jsonArray.getString(j));
                Log.d("DEBUG", "" + users);
                meetingInfo.setUsers(users);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            Log.d("DEBUG", "ServerAPI");
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());
            handleRefresh(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        Log.d("DEBUG", "refreshing");
        serverAPI.getMeeting(meetingInfo.getId());
    }
}
