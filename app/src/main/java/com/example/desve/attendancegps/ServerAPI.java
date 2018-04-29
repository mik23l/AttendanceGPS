package com.example.desve.attendancegps;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
    UserRatesActivity userRatesActivity;
    AnalyticsActivity analyticsActivity;

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

    public ServerAPI(WelcomeActivity ma) {
        welcomeActivity = ma;
        queue = Volley.newRequestQueue(welcomeActivity);
    }

    public ServerAPI(UserRatesActivity ma) {
        userRatesActivity = ma;
        queue = Volley.newRequestQueue(userRatesActivity);
    }

    public ServerAPI(AnalyticsActivity ma) {
        analyticsActivity = ma;
        queue = Volley.newRequestQueue(analyticsActivity);
    }


//    public void getUsers() {
//        StringRequest request = new StringRequest(Request.Method.GET, getURL(), this, this);
//        queue.add(request);
//    }

    public String getURL() {
        return requestURL;
    }

    public void login(String username, String password) {
        try {
            // Encode parameters that may contain spaces
            String username_encoded = URLEncoder.encode(username, "UTF-8");
            String password_encoded = URLEncoder.encode(password, "UTF-8");
            StringRequest request = new StringRequest(Request.Method.GET, getURL() + "login?user=" + username_encoded + "&pass=" + password_encoded, mainActivity, mainActivity);
            queue.add(request);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");

        }
    }

    public void createNewUser(String username, String password, String name) {
        try {
            // Encode parameters that may contain spaces
            String username_encoded = URLEncoder.encode(username, "UTF-8");
            String password_encoded = URLEncoder.encode(password, "UTF-8");
            String name_encoded = URLEncoder.encode(name, "UTF-8");
            StringRequest request = new StringRequest(Request.Method.GET, getURL() +
                    "user/add?user=" + username_encoded +
                    "&pass=" + password_encoded + "&name=" + name_encoded,
                    newUserActivity, newUserActivity);
            queue.add(request);

        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");

        }
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

    public void startMeeting(String s, LatLng latLng, String owner, String organization) {

        try {
            // Encode parameters that may contain spaces
            String meeting_name_encoded = URLEncoder.encode(s, "UTF-8");
            String organization_encoded = URLEncoder.encode(organization, "UTF-8");
            StringRequest request = new StringRequest(Request.Method.GET, getURL() +
                    "start_meeting?meeting_name=" + meeting_name_encoded +
                    "&lat=" + latLng.latitude + "&lon=" + latLng.longitude +
                    "&owner=" + owner + "&org=" + organization_encoded,
                    hostMeetingActivity, hostMeetingActivity);
            queue.add(request);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");

        }
    }

    public void joinMeeting(String meetingID, String username) {

        try {
            // Encode parameters that may contain spaces
            String username_encoded = URLEncoder.encode(username, "UTF-8");
            StringRequest request = new StringRequest(Request.Method.GET, getURL() + "join_meeting?meeting=" + meetingID + "&user=" + username_encoded, joinDetailsActivity, joinDetailsActivity);
            queue.add(request);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");

        }
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

    public void getOwnerOrgs(String id) {
        StringRequest request = new StringRequest(Request.Method.GET, getURL() + "data/hosted_orgs?owner=" + id, userRatesActivity, userRatesActivity);
        StringRequest request2 = new StringRequest(Request.Method.GET, getURL() + "data/hosted_orgs?owner=" + id, analyticsActivity, analyticsActivity);
        queue.add(request);
        queue.add(request2);
    }

    public void getAttendOrgs(String id) {
        StringRequest request = new StringRequest(Request.Method.GET,getURL() + "data/attended_orgs?owner=" + id, analyticsActivity, analyticsActivity);
        queue.add(request);
    }

    public void getUserRates(String id, String org) {
        StringRequest request;
        if (org != null) {
            request = new StringRequest(Request.Method.GET, getURL() +
                    "data/user_rates?owner=" + id + "&org=" + org, userRatesActivity, userRatesActivity);

        }
        else {
            request = new StringRequest(Request.Method.GET, getURL() +
                    "data/user_rates?owner=" + id, userRatesActivity, userRatesActivity);
        }
        queue.add(request);
    }

    public void getAttendMeetings(String id, String  org) {
        StringRequest request;
        if (org != null) {
            request = new StringRequest(Request.Method.GET, getURL() +
                    "data/i_attended?owner=" + id + "&org=" + org, analyticsActivity, analyticsActivity);
        } else {
            request = new StringRequest(Request.Method.GET, getURL() +
                    "data/i_attended?owner=" + id, analyticsActivity, analyticsActivity);
        }
       queue.add(request);
    }

    public void getHostMeetings(String id, String  org) {
        StringRequest request;
        if (org != null) {
            request = new StringRequest(Request.Method.GET, getURL() +
                    "data/i_hosted?owner=" + id + "&org=" + org, analyticsActivity, analyticsActivity);
        } else {
            request = new StringRequest(Request.Method.GET, getURL() +
                    "data/i_hosted?owner=" + id, analyticsActivity, analyticsActivity);
        }
        queue.add(request);
    }
}
