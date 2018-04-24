package com.example.desve.attendancegps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Samuel McGhee on 4/18/2018.
 */

public class UserInfo implements Serializable {
    String m_id;
    String m_username;
    String m_password;
    String m_name;

    public UserInfo(String id, String username, String pass, String name) {
        m_id = id;
        m_username = username;
        m_password = pass;
        m_name = name;
    }

    public UserInfo(JSONObject object) {
        try {
            m_name = object.getString("name");
            m_id = object.getString("id");
            m_username = object.getString("username");
            m_password = object.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
