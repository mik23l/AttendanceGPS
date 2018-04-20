package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinDetailsActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {

    TextView meetingName;
    MeetingInfo meetingInfo;
    ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_details);

        serverAPI = new ServerAPI(this);

        meetingName = findViewById(R.id.meeting_name);

        Intent intent = getIntent();
        meetingInfo = (MeetingInfo) intent.getSerializableExtra("MEETING");

        serverAPI.joinMeeting(String.valueOf(meetingInfo.getId()), "5648554290839552");
        meetingName.setText(meetingInfo.getName());
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
