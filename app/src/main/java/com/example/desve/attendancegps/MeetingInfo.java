package com.example.desve.attendancegps;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel McGhee on 4/20/2018.
 */

public class MeetingInfo implements Serializable {

    String id;
    String name;
    String startDate;
    String duration;
    String num_users;
    List<UserInfo> users;
    Double lat, lon;
    String owner;
    UserInfo owner_data;
    Boolean active = false;
    String org;

    public MeetingInfo(JSONObject object) {
        try {
            name = object.getString("name");
            lat = object.getDouble("lat");
            lon = object.getDouble("lon");
            id = object.getString("id");
            startDate = object.getString("date");
            org = object.getString("organization");
            active = object.getBoolean("active");
            owner_data = new UserInfo(object.getJSONObject("owner"));
            owner = owner_data.m_id;

            if (object.has("users")) {
                users = new ArrayList<>();
                JSONArray jsonArray = (JSONArray) object.get("users");
                for (int j = 0; j < jsonArray.length(); j++) {
                    UserInfo userInfo = new UserInfo(jsonArray.getJSONObject(j));
                    users.add(userInfo);
                }
                Log.d("DEBUG", "UserInfo = " + users);
                num_users = Integer.toString(users.size());
            } else {
                num_users = "--";
            }

            if (object.has("duration")) {
                duration = object.getString("duration");
            }
            else {
                duration = "On going";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Boolean getActive() {
        return active;
    }

    public MeetingInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public LatLng getCoor() {
        return new LatLng(lat, lon);
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOwnerName() {
        return owner_data.m_name;
    }

    public String getDuration() { return duration; }

    public void setDuration(String duration) { this.duration = duration; }

    public String getNum_users() { return num_users; }

    public void setNum_users(String num_users) { this.num_users = num_users; }
}
