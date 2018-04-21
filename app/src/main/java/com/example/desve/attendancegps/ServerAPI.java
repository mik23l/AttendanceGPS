package com.example.desve.attendancegps;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Desve on 4/11/2018.
 */

public class ServerAPI {

    String requestURL = "https://mobileapps-199414.appspot.com/";

    MainActivity mainActivity;
    NewUserActivity newUserActivity;
    JoinMeetingActivity joinMeetingActivity;
    HostMeetingActivity hostMeetingActivity;
    HostDetailsActivity hostDetailsActivity;
    JoinDetailsActivity joinDetailsActivity;
    WelcomeActivity welcomeActivity;

    RequestQueue queue;

    public ServerAPI(JoinDetailsActivity ma) {
        joinDetailsActivity = ma;
        queue = Volley.newRequestQueue(joinDetailsActivity);
    }

    public ServerAPI(HostDetailsActivity ma) {
        hostDetailsActivity = ma;
        queue = Volley.newRequestQueue(hostDetailsActivity);
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

    public  ServerAPI(WelcomeActivity ma) {
        welcomeActivity = ma;
        queue = Volley.newRequestQueue(welcomeActivity);
    }



//    public void getUsers() {
//        StringRequest request = new StringRequest(Request.Method.GET, getURL(), this, this);
//        queue.add(request);
//    }

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

    public void getNearbyMeetings(LatLng latLng) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "nearby_meetings?lat=" + latLng.latitude + "&lon=" + latLng.longitude , joinMeetingActivity, joinMeetingActivity);
        queue.add(request);
    }

    public void getMyActiveMeetings(String owner) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "my_active?owner=" + owner , welcomeActivity, welcomeActivity);
        queue.add(request);
    }

    // FIXME add owner parameter
    public void startMeeting(String s, LatLng latLng, String owner, String organization) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() +
                "start_meeting?meeting_name=" + s +
                "&lat=" + latLng.latitude + "&lon=" + latLng.longitude +
                "&owner=" + owner + "&org=" + organization,
                hostMeetingActivity, hostMeetingActivity);
        queue.add(request);
    }

    public void joinMeeting(String meetingID, String username) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "join_meeting?meeting=" + meetingID + "&user=" + username, joinDetailsActivity, joinDetailsActivity);
        queue.add(request);
    }

    public void getMeeting(String id) {
        Log.d("DEBUG", "ID = " + id);
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "meeting/" + id , joinDetailsActivity, joinDetailsActivity);
        queue.add(request);
    }

    public void getMeeting2(String id) {
        Log.d("DEBUG", "ID = " + id);
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "meeting/" + id , hostDetailsActivity, hostDetailsActivity);
        queue.add(request);
    }

    public void endMeeting(String id) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "meeting/" + id + "/end_meeting", hostDetailsActivity, hostDetailsActivity);
        queue.add(request);
    }
}
