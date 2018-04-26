package com.example.desve.attendancegps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UserInfoAdapter extends ArrayAdapter<UserInfo> {

    public static final DecimalFormat df1 = new DecimalFormat( "#.##" );

    private Context mContext;
    int mResource;
    int total_meetings = 0;

    public UserInfoAdapter(Context context, int resource, ArrayList<UserInfo> objects, int tm) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        total_meetings = tm;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the meeting information
        String name = getItem(position).m_name;
        int num_meetings = getItem(position).num_attended;
        double percent = 100.0 * ((double)num_meetings / (double) total_meetings);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.textView1);
        TextView tvNum  = convertView.findViewById(R.id.textView2);
        TextView tvPerc = convertView.findViewById(R.id.textView3);

        tvName.setText(name);
        tvNum.setText(String.valueOf(num_meetings));
        tvPerc.setText(String.format("%.2f", percent) + '%');

        return convertView;

    }
}
