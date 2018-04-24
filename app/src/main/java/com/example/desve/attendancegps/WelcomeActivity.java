package com.example.desve.attendancegps;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {

    TextView welcomeText;

    DatabaseManager databaseManager;

    ServerAPI serverAPI;

    Button hostButton;
    Button currentMeeting;
    LinearLayout layout;
    UserInfo userInfo;
    MeetingInfo meetingInfo;

    // RESULTS CODES
    static final int HOST_MEETING = 1;
    static final int HOST_DETAILS = 2;
    static final int JOIN_MEETING = 3;
    static final int JOIN_DETAILS = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        hostButton  = findViewById(R.id.host_button);
        layout      = findViewById(R.id.linear_layout);
        welcomeText = findViewById(R.id.welcome_text);
        currentMeeting = findViewById(R.id.currentButton);


        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        // Get username from database and set welcome message
        userInfo = databaseManager.getUserFromDB();
        welcomeText.setText("Welcome, " + userInfo.m_username);

        serverAPI = new ServerAPI (this);
        serverAPI.getMyActiveMeetings(userInfo.m_id);
    }

    @Override
    protected void onResume() {
        Log.d("DEBUG", "WelcomeActivity : OnResume");
        serverAPI.getMyActiveMeetings(userInfo.m_id);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", "WelcomeActivity : OnActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HOST_MEETING && resultCode == RESULT_OK) {
            Log.d("DEBUG", "Welcome : result host meeting");

            MeetingInfo mInfo = (MeetingInfo) data.getExtras().getSerializable("MEETING");

            Log.d("DEBUG", "meeting info = " + mInfo);

            if (mInfo != null) {
                Intent myIntent = new Intent(this, HostDetailsActivity.class);
                myIntent.putExtra("MEETING", mInfo);
                startActivityForResult(myIntent, HOST_DETAILS);
            }

        }
        else if (requestCode == HOST_DETAILS && resultCode == RESULT_OK) {
            Log.d("DEBUG", "Welcome : result host details");
        }
        else if (requestCode == JOIN_MEETING && resultCode == RESULT_OK) {
            Log.d("DEBUG", "Welcome : result join meeting");
            MeetingInfo mInfo = (MeetingInfo) data.getExtras().getSerializable("MEETING");

            Log.d("DEBUG", "meeting info = " + mInfo);

            if (mInfo != null) {
                Intent myIntent = new Intent(this, JoinDetailsActivity.class);
                myIntent.putExtra("MEETING", mInfo);
                startActivityForResult(myIntent, JOIN_DETAILS);
            }
        }
        else if (requestCode == JOIN_DETAILS && resultCode == RESULT_OK) {
            Log.d("DEBUG", "Welcome : result join details");
        }
    }

    // On host meeting button clicked
    public void hostMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, HostMeetingActivity.class);
        startActivityForResult(myIntent, HOST_MEETING);
    }

    // On join meeting button clicked
    public void joinMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, JoinMeetingActivity.class);
        startActivityForResult(myIntent, JOIN_MEETING);
    }

    // On analytics button clicked
    public void analytics(View view) {
        // FIXME Need to start analytics activity here
        Intent myIntent = new Intent(WelcomeActivity.this, AnalyticsActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    // On sign out button clicked
    public void signOut(View view) {
        databaseManager.deleteAll();
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            Log.d("DEBUG","Checking for active meetings...");
            Log.d("DEBUG",response);

            final JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("NoActive")) {
                Log.d("DEBUG", "No Current Meeting");
                hostButton.setAlpha(1);
                hostButton.setClickable(true);
                currentMeeting.setClickable(false);
                currentMeeting.setVisibility(View.INVISIBLE);
            }
            else {
                Log.d("DEBUG", jsonObject.toString());
                hostButton.setAlpha(.5f);
                hostButton.setClickable(false);
                currentMeeting.setClickable(true);
                currentMeeting.setVisibility(View.VISIBLE);
                meetingInfo = new MeetingInfo(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onCurrentClicked(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, HostDetailsActivity.class);
        myIntent.putExtra("MEETING", meetingInfo);
        WelcomeActivity.this.startActivityForResult(myIntent, HOST_DETAILS);
    }
}
