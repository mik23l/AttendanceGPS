package com.example.desve.attendancegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeText = (TextView) findViewById(R.id.welcome_text);

        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");

        welcomeText.setText("Welcome, " + username);
    }

    public void hostMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, HostMeetingActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    public void joinMeeting(View view) {
        Intent myIntent = new Intent(WelcomeActivity.this, JoinMeetingActivity.class);
        WelcomeActivity.this.startActivity(myIntent);
    }

    public void analytics(View view) {
    }

    public void signOut(View view) {
        
    }
}
