package com.example.desve.attendancegps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserRatesActivity extends Activity implements Response.Listener<String>, Response.ErrorListener {

    UserInfo userInfo;
    ServerAPI serverAPI;
    DatabaseManager databaseManager;

    Spinner orgSpinner;
    TextView meetingCount;
    ListView userListView;

    List<String> organizations;
    ArrayList<UserInfo> userInfoRates;

    int num_meetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rates);

        orgSpinner = findViewById(R.id.org_spin);
        meetingCount = findViewById(R.id.meeting_count);
        userListView = findViewById(R.id.user_list);

        serverAPI = new ServerAPI(this);
        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        userInfo = databaseManager.getUserFromDB();
        databaseManager.close();

        organizations = new ArrayList<>();
        organizations.add("No Filter");
        userInfoRates = new ArrayList<>();

        meetingCount.setText("");

        serverAPI.getOwnerOrgs(userInfo.m_id);
        serverAPI.getUserRates(userInfo.m_id, null);
    }

    private void populateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, organizations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgSpinner.setAdapter(adapter);
    }

    private void populateUserList() {
        UserInfoAdapter userInfoAdapter = new UserInfoAdapter(this,R.layout.user_adapter_layout, userInfoRates, num_meetings);
        userListView.setAdapter(userInfoAdapter);
    }

    public void onClickFilter(View view) {
        String orgfilter = (String) orgSpinner.getSelectedItem();
        if (orgfilter.equals("No Filter")) {
            serverAPI.getUserRates(userInfo.m_id, null);
        }
        else {
            serverAPI.getUserRates(userInfo.m_id, orgfilter);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());

            if (jsonObject.has("hosted")) {
                Log.d("DEBUG", "orgs list");
                JSONArray jsonList = jsonObject.getJSONArray("organization");
                organizations.clear();
                organizations.add("No Filter");
                for (int i=0; i<jsonList.length(); i++) {
                    organizations.add(jsonList.getString(i));
                }
                populateSpinner();
            }
            else {
                Log.d("DEBUG", "user info stuff");
                num_meetings = jsonObject.getJSONArray("meetings").length();
                meetingCount.setText(String.valueOf(num_meetings));
                userInfoRates.clear();
                JSONArray users = jsonObject.getJSONArray("users");
                for (int i=0; i< users.length(); i++){
                    userInfoRates.add(new UserInfo(users.getJSONObject(i)));
                }
                populateUserList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
