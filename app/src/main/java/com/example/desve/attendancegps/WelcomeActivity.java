package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeText;

    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeText = (TextView) findViewById(R.id.welcome_text);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        // Get username from database and set welcome message
        UserInfo userInfo = databaseManager.getUserFromDB();
        welcomeText.setText("Welcome, " + userInfo.m_username);

    }

    // On host meeting button clicked
    public void hostMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, HostMeetingActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    // On join meeting button clicked
    public void joinMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, JoinMeetingActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    // On analytics button clicked
    public void analytics(View view) {
        // FIXME Need to start analytics activity here
    }

    // On sign out button clicked
    public void signOut(View view) {
        databaseManager.deleteAll();
        Intent myIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }
}
