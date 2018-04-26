package com.example.desve.attendancegps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends Activity implements View.OnClickListener {

    Spinner attend_spin;
    Button attend_filter;
    ListView attend_list;
    Spinner host_spin;
    Button host_filter;
    ListView host_list;

    DatabaseManager databaseManager;
    ServerAPI serverAPI;

    ArrayList<MeetingObject> meetingList = new ArrayList<>();
    List<String> organizationList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        attend_spin=(Spinner)findViewById(R.id.attend_spin);
        attend_filter=(Button)findViewById(R.id.attend_filter);
        attend_list=(ListView)findViewById(R.id.attend_list);
        host_spin=(Spinner)findViewById(R.id.host_spin);
        host_filter=(Button)findViewById(R.id.host_filter);
        host_list=(ListView)findViewById(R.id.host_list);

        attend_filter.setOnClickListener(this);
        host_filter.setOnClickListener(this);

        // Create the Meeting object
        MeetingObject meeting1 = new MeetingObject("Meeting 1", "CS 3714", "4/25/2018", "1 hr 20 min", "45");
        MeetingObject meeting2 = new MeetingObject("Meeting 1", "CS 3714", "4/25/2018", "1 hr 20 min", "45");
        MeetingObject meeting3 = new MeetingObject("Meeting 1", "CS 3714", "4/25/2018", "1 hr 20 min", "45");
        MeetingObject meeting4 = new MeetingObject("Meeting 1", "CS 3714", "4/25/2018", "1 hr 20 min", "45");

        // Add the Meeting object to an ArrayList
        meetingList.add(meeting1);
        meetingList.add(meeting2);
        meetingList.add(meeting3);
        meetingList.add(meeting4);

        MeetingListAdapter adapter = new MeetingListAdapter(this,R.layout.adapter_view_layout, meetingList);
        attend_list.setAdapter(adapter);
        host_list.setAdapter(adapter);

        organizationList.add("CS 3714");
        organizationList.add("CHEM 1036");
        organizationList.add("ECE 3574");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, organizationList);
        attend_spin.setAdapter(dataAdapter);
        host_spin.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View view) {

    }


    // by sam
    public void onClickAttendeeAverages(View view) {
        Log.d("DEBUG", "Starting user rates activity");
        Intent intent = new Intent(this, UserRatesActivity.class);
        startActivity(intent);
    }
}
