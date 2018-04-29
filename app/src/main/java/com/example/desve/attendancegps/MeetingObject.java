package com.example.desve.attendancegps;

/**
 * Created by Michaela on 4/25/2018.
 */

public class MeetingObject {

    private String name;
    private String organization;
    private String date;
    private String duration;
    private String attendees;

    public MeetingObject(String name, String organization, String date, String duration, String attendees) {
        this.name = name;
        this.organization = organization;
        this.date = date;
        this.duration = duration;
        this.attendees = attendees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }
}