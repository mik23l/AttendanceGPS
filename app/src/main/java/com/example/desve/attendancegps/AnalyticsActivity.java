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

    ArrayList<MeetingObject> meetingAttendList;
    ArrayList<MeetingObject> meetingHostList;
    List<String> orgAttendList;
    List<String> orgHostList;

    int avg_attendees;

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
        MeetingListAdapter hostadapter = new MeetingListAdapter(this,R.layout.adapter_view_layout, meetingHostList);
        host_list.setAdapter(hostadapter);
    }
    private void populateAttendList() {
        MeetingListAdapter attendadapter = new MeetingListAdapter(this,R.layout.adapter_view_layout, meetingAttendList);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
