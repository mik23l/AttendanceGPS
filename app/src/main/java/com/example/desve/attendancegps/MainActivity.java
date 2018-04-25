package com.example.desve.attendancegps;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


/*
Sams API key = AIzaSyD1S--hFyAhoxJyfGSmuUOrEwH1HVIk_7c

 */

public class MainActivity extends Activity implements Response.Listener<String>, Response.ErrorListener {

    EditText usernameEditText;
    EditText passwordEditText;
    Button   submitButton;
    Button   newUserButton;

    ServerAPI       serverAPI;
    DatabaseManager databaseManager;

    static final int NEW_USER_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        submitButton     = findViewById(R.id.submit_button);
        newUserButton    = findViewById(R.id.new_user_button);

        serverAPI = new ServerAPI (this);
        databaseManager = new DatabaseManager(this);

        // Check if user already exists in the database and attempt to sign in
        databaseManager.open();
        UserInfo userInfo = databaseManager.getUserFromDB();

        Log.d("debug", "Loaded: " + userInfo.m_username);
        if (!userInfo.m_username.equals("")) {
            Log.d("debug", "Using database login");
            // Set username and password while loading
            usernameEditText.setText(userInfo.m_username);
            passwordEditText.setText(userInfo.m_password);

            serverAPI.login(userInfo.m_username, userInfo.m_password);
        }
    }

    public void onSubmit(View view) {

        // Check if username and password are not blank
        if(!TextUtils.isEmpty(usernameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText())) {

            // Make a request to server to validate username and password
            serverAPI.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        }
        else { // Username or password field is blank
            Toast.makeText(getApplicationContext(), "One or more field left blank!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == NEW_USER_RESULT) {
            UserInfo userInfo = databaseManager.getUserFromDB();
            Log.d("debug", "Loaded: " + userInfo.m_username);
            if (!userInfo.m_username.equals("")) {
                Log.d("debug", "Using database login");
                // Set username and password while loading
                usernameEditText.setText(userInfo.m_username);
                passwordEditText.setText(userInfo.m_password);

                serverAPI.login(userInfo.m_username, userInfo.m_password);
            }
        }
    }

    // New user button clicked
    public void onNewUser (View view) {
        Intent myIntent = new Intent(MainActivity.this, NewUserActivity.class);
        startActivityForResult(myIntent, NEW_USER_RESULT);
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());

            // Check if response was true or false
            Boolean success = jsonObject.getBoolean("success"); // FIXME need to also check if the username doesn't exist yet
            if(success) {

                JSONObject userObject = jsonObject.getJSONObject("user");
                Log.d("DEBUG", userObject.toString());

                // Remove current user from local database
                databaseManager.deleteAll();
                // Insert new user into local database
                databaseManager.insertUserInfo(userObject.getString("id"),
                        userObject.getString("username"),
                        userObject.getString("password"),
                        userObject.getString("name"));

                // Start welcome activity
                Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
            else {
                Log.d("DEBUG", "Toasting...");
                Toast.makeText(getApplicationContext(), "Incorrect username or password!", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
