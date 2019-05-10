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
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends Activity implements Response.Listener<String>, Response.ErrorListener {

    Spinner attend_spin;
    ListView attend_list;
    Spinner host_spin;
    ListView host_list;
    TextView avg_attend;

    DatabaseManager databaseManager;
    ServerAPI serverAPI;
    UserInfo userInfo;
    MeetingInfo meetingInfo;

    ArrayList<MeetingInfo> meetingAttendList;
    ArrayList<MeetingInfo> meetingHostList;
    ArrayList<MeetingObject> AttendList;
    ArrayList<MeetingObject> HostList;
    List<String> orgAttendList;
    List<String> orgHostList;

    int avg_attendees;
    int total_meetings;
    int total_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        attend_spin=(Spinner)findViewById(R.id.attend_spin);
        attend_list=(ListView)findViewById(R.id.attend_list);
        host_spin=(Spinner)findViewById(R.id.host_spin);
        host_list=(ListView)findViewById(R.id.host_list);
        avg_attend=(TextView)findViewById(R.id.avg_attend);

        serverAPI = new ServerAPI(this);
        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        userInfo = databaseManager.getUserFromDB();
        databaseManager.close();

        meetingAttendList = new ArrayList<>();
        meetingHostList = new ArrayList<>();
        AttendList = new ArrayList<>();
        HostList = new ArrayList<>();
        orgAttendList = new ArrayList<>();
        orgAttendList.add("No Filter");
        populateAttendSpinner();
        orgHostList = new ArrayList<>();
        orgHostList.add("No Filter");
        populateHostSpinner();

        serverAPI.getOwnerOrgs2(userInfo.m_id);
        serverAPI.getAttendOrgs(userInfo.m_id);
        serverAPI.getHostMeetings(userInfo.m_id, null);
        serverAPI.getAttendMeetings(userInfo.m_id, null);

        Intent intent = getIntent();
        meetingInfo = (MeetingInfo) intent.getSerializableExtra("MEETING");
    }

    private void populateHostSpinner() {
        ArrayAdapter<String> orgHostAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orgHostList);
        orgHostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        host_spin.setAdapter(orgHostAdapter);
    }
    private void populateAttendSpinner() {
        ArrayAdapter<String> orgAttendAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orgAttendList);
        orgAttendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attend_spin.setAdapter(orgAttendAdapter);
    }

    private void populateHostList() {
        total_users = 0;
        HostList.clear();
        for(MeetingInfo meetingInfo : meetingHostList) {
            MeetingObject meeting = new MeetingObject(meetingInfo.getName(), meetingInfo.getOrg(), meetingInfo.getStartDate(), meetingInfo.getDuration(), meetingInfo.getNum_users());
            HostList.add(meeting);
            total_users = total_users + (Integer.parseInt(meetingInfo.getNum_users()));
        }
        if(total_meetings == 0) {
            avg_attendees = 0;
        } else {
            avg_attendees = total_users/total_meetings;
        }
        avg_attend.setText(String.valueOf(avg_attendees));
        MeetingListAdapter hostadapter = new MeetingListAdapter(this,R.layout.adapter_view_layout, HostList);
        host_list.setAdapter(hostadapter);
    }
    private void populateAttendList() {
        AttendList.clear();
        for(MeetingInfo meetingInfo : meetingAttendList) {
            MeetingObject meeting = new MeetingObject(meetingInfo.getName(), meetingInfo.getOrg(), meetingInfo.getStartDate(), meetingInfo.getDuration(), meetingInfo.getNum_users());
            AttendList.add(meeting);

        }
        MeetingListAdapter attendadapter = new MeetingListAdapter(this,R.layout.adapter_view_layout, AttendList);
        attend_list.setAdapter(attendadapter);
    }


    public void onClickFilterAttend(View view) {
        String orgfilter = (String) attend_spin.getSelectedItem();
        if (orgfilter.equals("No Filter")) {
            serverAPI.getAttendMeetings(userInfo.m_id, null);
        } else {
            serverAPI.getAttendMeetings(userInfo.m_id,orgfilter);
        }
    }
    public void onClickFilterHost(View view) {
        String orgfilter = (String) host_spin.getSelectedItem();
        if (orgfilter.equals("No Filter")) {
            serverAPI.getHostMeetings(userInfo.m_id, null);
        } else {
            serverAPI.getHostMeetings(userInfo.m_id, orgfilter);
        }
    }

    // by sam
    public void onClickAttendeeAverages(View view) {
        Log.d("DEBUG", "Starting user rates activity");
        Intent intent = new Intent(this, UserRatesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());

            if(jsonObject.has("attended")) {
                Log.d("DEBUG", "attend orgs list");
                JSONArray jsonList = jsonObject.getJSONArray("attended");
                orgAttendList.clear();
                orgAttendList.add("No Filter");
                for(int i = 0; i<jsonList.length();i++) {
                    orgAttendList.add(jsonList.getString(i));
                }
                populateAttendSpinner();
            }

            if(jsonObject.has("hosted")) {
                Log.d("DEBUG", "hosted orgs list");
                JSONArray jsonList = jsonObject.getJSONArray("hosted");
                orgHostList.clear();
                orgHostList.add("No Filter");
                for(int i = 0; i<jsonList.length();i++) {
                    orgHostList.add(jsonList.getString(i));
                }
                populateHostSpinner();
            }

            if(jsonObject.has("meetings_attended")) {
                Log.d("DEBUG", "attended meetings");
                meetingAttendList.clear();
                JSONArray list = jsonObject.optJSONArray("meetings_attended");
                for(int i = 0; i <list.length(); i++) {
                    JSONObject attend_meeting = list.optJSONObject(i);
                    MeetingInfo m = new MeetingInfo(attend_meeting);
                    meetingAttendList.add(m);
                }
                populateAttendList();
            }
            if(jsonObject.has("meetings_hosted")) {
                Log.d("DEBUG", "hosted meetings");
                total_meetings = jsonObject.getJSONArray("meetings_hosted").length();
                meetingHostList.clear();
                JSONArray list = jsonObject.optJSONArray("meetings_hosted");
                for(int i = 0; i <list.length(); i++) {
                    JSONObject attend_meeting = list.optJSONObject(i);
                    MeetingInfo m = new MeetingInfo(attend_meeting);
                    meetingHostList.add(m);
                }
                populateHostList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}