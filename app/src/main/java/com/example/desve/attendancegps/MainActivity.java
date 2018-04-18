package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {

    EditText usernameEditText;
    EditText passwordEditText;
    Button submitButton;

    Button newUserButton;

    ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        submitButton = (Button) findViewById(R.id.submit_button);

        newUserButton = (Button) findViewById(R.id.new_user_button);

        serverAPI = new ServerAPI(this);


    }

    public void onSubmit(View view) {
        Log.d("DEBUG", usernameEditText.getText().toString());
        Log.d("DEBUG", passwordEditText.getText().toString());

        // Check if username and password are blank
        if(!TextUtils.isEmpty(usernameEditText.getText()) && !TextUtils.isEmpty(passwordEditText.getText())) {

            // Make a request to server to validate username and password
            serverAPI.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());

        }
        else {
            Toast.makeText(getApplicationContext(), "One or more field left blank!", Toast.LENGTH_LONG).show();
        }
    }

    // New user button clicked
    public void onNewUser (View view) {
        Intent myIntent = new Intent(MainActivity.this, NewUserActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            Log.d("DEBUG", "MainActivity");
            JSONObject jsonObject = new JSONObject(response);
            Log.d("DEBUG", jsonObject.toString());

            // Check if response was true or false
            Boolean success = jsonObject.getBoolean("success"); // FIXME need to also check if the username doesn't exist yet
            if(success) {

                Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                myIntent.putExtra("USERNAME", usernameEditText.getText().toString());
                MainActivity.this.startActivity(myIntent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Incorrect username or password!", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
