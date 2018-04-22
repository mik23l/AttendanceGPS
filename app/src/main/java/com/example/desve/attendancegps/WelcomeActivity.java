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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        hostButton  = findViewById(R.id.host_button);
        layout      = findViewById(R.id.linear_layout);
        welcomeText = findViewById(R.id.welcome_text);


        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        // Get username from database and set welcome message
        UserInfo userInfo = databaseManager.getUserFromDB();
        welcomeText.setText("Welcome, " + userInfo.m_username);

        serverAPI = new ServerAPI (this);
        serverAPI.getMyActiveMeetings(userInfo.m_id);
    }

    // On host meeting button clicked
    public void hostMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, HostMeetingActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    // On join meeting button clicked
    public void joinMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, JoinMeetingActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    // On analytics button clicked
    public void analytics(View view) {
        // FIXME Need to start analytics activity here
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

            if(response.equals("false")) {

            }
            else {
                final JSONObject jsonObject = new JSONObject(response);
                Log.d("DEBUG", jsonObject.toString());
                hostButton.setAlpha(.5f);
                hostButton.setClickable(false);

                currentMeeting = new Button(this);
                currentMeeting.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent myIntent = new Intent(WelcomeActivity.this, HostDetailsActivity.class);
                        MeetingInfo meetingInfo = new MeetingInfo(jsonObject);
                        myIntent.putExtra("MEETING", meetingInfo);
                        WelcomeActivity.this.startActivity(myIntent);
                    }
                });
                currentMeeting.setText("Current Meeting");
                currentMeeting.setTextColor(Color.WHITE);
                currentMeeting.setBackgroundColor(Color.RED);
                currentMeeting.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                layout.addView(currentMeeting);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
