package com.itgapps.appidol.ListViewAdapters;


import com.itgapps.appidol.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TracksListViewAdapter extends BaseAdapter{
	
	private Context mContext;
    Cursor cursor;
    
    public TracksListViewAdapter(Context context,Cursor cur) 
    {
            super();
            mContext=context;
            cursor=cur;
           
    }
       
    public int getCount() 
    {
        // return the number of records in cursor
        return cursor.getCount();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent) 
    {
                    // inflate the layout for each item of listView
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_tracks, null);
            
                    // move the cursor to required position 
                    cursor.moveToPosition(position);
                    
                    // fetch the sender number and sms body from cursor

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String numTrack = cursor.getString(cursor.getColumnIndex("numTrack"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String duration = cursor.getString(cursor.getColumnIndex("duration"));
                   
                    // get the reference of textViews and imageView
                    TextView textId = (TextView)view.findViewById(R.id.track_id);
                    TextView textNroTrack = (TextView)view.findViewById(R.id.track_number);
                    TextView textName = (TextView)view.findViewById(R.id.track_name);
                    TextView textDuration = (TextView)view.findViewById(R.id.track_duration);
                    
                    // Set the Sender number and smsBody to respective TextViews 
                    textId.setText(id);
                    textNroTrack.setText(numTrack + " -");
                    textName.setText(name);
                    textDuration.setText(duration);
                    
                    return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}