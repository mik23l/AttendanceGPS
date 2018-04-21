package com.example.desve.attendancegps;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class HostDetailsActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener,SwipeRefreshLayout.OnRefreshListener {

    TextView meetingName;
    TextView orgText;
    TextView ownerText;
    TextView startText;
    MeetingInfo meetingInfo;
    ServerAPI serverAPI;
    LinearLayout userLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    UserInfo userInfo;

    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_details);

        meetingInfo = (MeetingInfo) getIntent().getSerializableExtra("MEETING");

        serverAPI = new ServerAPI(this);

        databaseManager = new DatabaseManager(this);

        // Check if user already exists in the database and attempt to sign in
        databaseManager.open();
        userInfo = databaseManager.getUserFromDB();
        databaseManager.close();

        meetingName = findViewById(R.id.meeting_name);
        orgText = findViewById(R.id.organization);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        startText = findViewById(R.id.start_time);
        ownerText = findViewById(R.id.owner_name);
        userLayout = findViewById(R.id.users_list);

        swipeRefreshLayout.setOnRefreshListener(this);
        meetingName.setText(meetingInfo.getName());
        updateUI();
    }

    public void askEndMeeting(View view) {
    }

    private void updateUI() {
        if (!meetingInfo.getOrg().equals("null")) {
            Log.d("DEBUG", "ORG = " + meetingInfo.getOrg());
            orgText.setText("Organization: " + meetingInfo.getOrg());
        } else {
            orgText.setText("");
        }

        if (meetingInfo.getOwner() != null) {
            ownerText.setText("Owner: " + meetingInfo.getOwner());
        } else {
            ownerText.setText("");
        }

        if (meetingInfo.getStartDate() != null) {
            startText.setText("Start Time: " + meetingInfo.getStartDate());
        } else {
            startText.setText("");
        }

        Log.d("DEBUG", "USERS = " + meetingInfo.getUsers());
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
        meetingInfo = new MeetingInfo(object);
        updateUI();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            handleRefresh(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        Log.d("DEBUG", "refreshing");
        serverAPI.getMeeting2(meetingInfo.getId());
    }
}
