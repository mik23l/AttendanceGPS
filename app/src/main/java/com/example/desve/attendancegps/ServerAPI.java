package com.example.desve.attendancegps;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Desve on 4/11/2018.
 */

public class ServerAPI implements Response.Listener<String>, Response.ErrorListener {

    String requestURL = "https://mobileapps-199414.appspot.com/";


    MainActivity mainActivity;
    NewUserActivity newUserActivity;
    JoinMeetingActivity joinMeetingActivity;
    HostMeetingActivity hostMeetingActivity;
    JoinDetailsActivity joinDetailsActivity;

    RequestQueue queue;

    public ServerAPI(JoinDetailsActivity ma) {
        joinDetailsActivity = ma;
        queue = Volley.newRequestQueue(joinDetailsActivity);
    }

    public ServerAPI(JoinMeetingActivity ma) {
        joinMeetingActivity = ma;
        queue = Volley.newRequestQueue(joinMeetingActivity);
    }

    public ServerAPI(HostMeetingActivity ma) {
        hostMeetingActivity = ma;
        queue = Volley.newRequestQueue(hostMeetingActivity);
    }

    public ServerAPI(MainActivity ma) {
        mainActivity = ma;
        queue = Volley.newRequestQueue(mainActivity);
    }

    public ServerAPI(NewUserActivity ma) {
        newUserActivity = ma;
        queue = Volley.newRequestQueue(newUserActivity);
    }

    public void getUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, getURL(), this, this);
        queue.add(request);
    }

    public String getURL() {
        return requestURL;
    }

    public void login(String username, String password) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "login?user=" + username + "&pass=" + password, mainActivity, mainActivity);
        queue.add(request);
    }

    public void createNewUser(String username, String password) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "user/add?user=" + username + "&pass=" + password, newUserActivity, newUserActivity);
        queue.add(request);
    }

    public void getMeetings() {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "meeting", joinMeetingActivity, joinMeetingActivity);
        queue.add(request);
    }

    // FIXME add owner parameter
    public void startMeeting(String s, LatLng latLng, String owner) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() +
                "start_meeting?meeting_name=" + s +
                "&lat=" + latLng.latitude + "&lon=" + latLng.longitude +
                "&owner=" + owner, hostMeetingActivity, hostMeetingActivity);
        queue.add(request);
    }

    public void joinMeeting(String meetingID, String userID) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "join_meeting?meeting=" + meetingID + "&user=" + userID, joinDetailsActivity, joinDetailsActivity);
        queue.add(request);
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
