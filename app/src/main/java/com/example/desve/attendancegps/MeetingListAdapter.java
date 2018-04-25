package com.example.desve.attendancegps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Michaela on 4/25/2018.
 */

public class MeetingListAdapter extends ArrayAdapter<MeetingObject> {

    private Context mContext;
    int mResource;

    public MeetingListAdapter(Context context, int resource, ArrayList<MeetingObject> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the meeting information
        String name=getItem(position).getName();
        String organization=getItem(position).getOrganization();
        String date=getItem(position).getDate();
        String duration=getItem(position).getDuration();
        String attendees=getItem(position).getAttendees();

        // Create the meeting object with the information
        MeetingObject meetingObject = new MeetingObject(name, organization, date, duration, attendees);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName=(TextView)convertView.findViewById(R.id.textView1);
        TextView tvOrgan=(TextView)convertView.findViewById(R.id.textView2);
        TextView tvDate=(TextView)convertView.findViewById(R.id.textView3);
        TextView tvDurat=(TextView)convertView.findViewById(R.id.textView4);
        TextView tvAtten=(TextView)convertView.findViewById(R.id.textView5);

        tvName.setText(name);
        tvOrgan.setText(organization);
        tvDate.setText(date);
        tvDurat.setText(duration);
        tvAtten.setText(attendees);

        return convertView;

    }
}
