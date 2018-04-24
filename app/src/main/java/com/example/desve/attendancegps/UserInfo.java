package com.example.desve.attendancegps;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Samuel McGhee on 4/18/2018.
 */

public class UserInfo {
    String m_id;
    String m_username;
    String m_password;
    String m_name;

    public UserInfo(String id, String username, String pass) {
        m_id = id;
        m_username = username;
        m_password = pass;
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
