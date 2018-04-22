package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class NewUserActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {

    EditText usernameEditText;
    EditText passwordEditText;
    EditText reenterEditText;

    ServerAPI serverAPI;
    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        usernameEditText = (EditText) findViewById(R.id.new_username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.new_password_edit_text);
        reenterEditText = (EditText) findViewById(R.id.new_reenter_password_edit_text);

        serverAPI = new ServerAPI(this);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

    }

    public void onCreateUser(View view) {

        // Check if username and password are blank
        if(!TextUtils.isEmpty(usernameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(reenterEditText.getText())) {

            // Check if passwords match
            if(!passwordEditText.getText().toString().equals(reenterEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                return;
            }

            // Make request to database to create new user
            serverAPI.createNewUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        }
        else {
            Toast.makeText(getApplicationContext(), "One or more field left blank!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            Log.d("DEBUG", jsonObject.toString());

            // Check if username is already in use
            if(!jsonObject.has("ERROR")) {

                // Remove current user from local database
                databaseManager.deleteAll();
                // Insert new user into local database
                databaseManager.insertUserInfo(jsonObject.getString("id"),
                        jsonObject.getString("username").toString(),
                        jsonObject.getString("password").toString());

                // Start welcome activity
                Intent myIntent = new Intent(NewUserActivity.this, WelcomeActivity.class);
                NewUserActivity.this.startActivity(myIntent);
            }

            else {
                Toast.makeText(getApplicationContext(), "Username already in use!", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

}
