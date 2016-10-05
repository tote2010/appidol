package com.itgapps.appidol.ListViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itgapps.appidol.R;

public class AlbumsListViewAdapter extends BaseAdapter{
	
	private Context mContext;
    Cursor cursor;
    
    public AlbumsListViewAdapter(Context context,Cursor cur) 
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
                    view = inflater.inflate(R.layout.list_albums, null);

                    // move the cursor to required position
                    cursor.moveToPosition(position);

                    // fetch the sender number and sms body from cursor

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String year = cursor.getString(cursor.getColumnIndex("year"));
                    String cover = cursor.getString(cursor.getColumnIndex("cover"));

                    // get the reference of textViews and imageView
                    TextView textId = (TextView)view.findViewById(R.id.album_id);
                    TextView textTitle= (TextView)view.findViewById(R.id.title);
                    TextView textYear= (TextView)view.findViewById(R.id.year);
                    ImageView imgCover = (ImageView)view.findViewById(R.id.cover);
                    
                    // Set the Sender number and smsBody to respective TextViews 
                    textId.setText(id);
                    textTitle.setText(name);
                    textYear.setText(year);
                    
                    if (cover.equals("disk1")) {
                    	imgCover.setImageResource(R.drawable.disk1);					
					} else if(cover.equals("disk2")){
						imgCover.setImageResource(R.drawable.disk2);
					} else if(cover.equals("disk3")){
						imgCover.setImageResource(R.drawable.disk3);
					} else if(cover.equals("disk4")){
						imgCover.setImageResource(R.drawable.disk4);
					} else if(cover.equals("disk5")){
						imgCover.setImageResource(R.drawable.disk5);
					} else if(cover.equals("disk6")){
						imgCover.setImageResource(R.drawable.disk6);
					}
        
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