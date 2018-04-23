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
    String endDate;
    String duration;
    List<String> users;
    Double lat, lon;
    String owner;
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

            if (object.has("users")) {
                users = new ArrayList<>();
                JSONArray jsonArray = (JSONArray) object.get("users");
                for (int j = 0; j < jsonArray.length(); j++)
                    users.add(jsonArray.getString(j));
                Log.d("DEBUG", "UserInfo = " + users);
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

    public List<String> getUsers() {
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
}
