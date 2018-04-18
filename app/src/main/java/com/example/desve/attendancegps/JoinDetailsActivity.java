package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinDetailsActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {

    TextView meetingName;

    ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_details);

        serverAPI = new ServerAPI(this);

        meetingName = (TextView) findViewById(R.id.meeting_name);

        Intent intent = getIntent();
        String meetingNameString = intent.getStringExtra("MEETING_NAME");

        meetingName.setText(meetingNameString);

        serverAPI.joinMeeting("5741031244955648","5648554290839552");
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
