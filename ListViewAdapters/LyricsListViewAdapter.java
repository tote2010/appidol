package com.itgapps.appidol.ListViewAdapters;


import com.itgapps.appidol.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LyricsListViewAdapter extends BaseAdapter{
	
	private Context mContext;
    Cursor cursor;

    
    String[] mProjection;
    private static String[] alphabetic = {"0","1","2","3","4","5","6","7","8","9","a", "b","c"};
    // State of the row that needs to show separator
    private static final int SECTIONED_STATE = 1;
    // State of the row that need not show separator
    private static final int REGULAR_STATE = 2;
    // Cache row states based on positions
    private int[] mRowStates;
    
    
    public LyricsListViewAdapter(Context context,Cursor cur) 
    {
            super();
            mContext=context;
            cursor=cur;
            mRowStates = new int[getCount()];
            
    }
       
    public int getCount() 
    {
        // return the number of records in cursor
        return cursor.getCount();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent) 
    {
    	boolean showSeparator = false;
    	
                    // inflate the layout for each item of listView
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_lyrics, null);
                    //cursor.moveToFirst();
                    //String alphabetPosition = alphabetic[0];
                    // move the cursor to required position 
                    cursor.moveToPosition(position);
                    
                    // fetch the sender number and sms body from cursor

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                   
                    // get the reference of textViews and imageView
                    TextView textId = (TextView)view.findViewById(R.id.lyric_id);
                    TextView textTitle= (TextView)view.findViewById(R.id.lyric_title);
                    
                    // Set the Sender number and smsBody to respective TextViews 
                    textId.setText(id);
                    textTitle.setText(name);
                        
                    // Show separator ?
                    switch (mRowStates[position]) {
         
                        case SECTIONED_STATE:
                            showSeparator = true;
                            break;
         
                        case REGULAR_STATE:
                            showSeparator = false;
                            break;
         
                        default:
         
                            if (position == 0) {
                                showSeparator = true;
                            }
                            else {
                                cursor.moveToPosition(position - 1);
         
                                String previousName = cursor.getString(cursor.getColumnIndex("name"));
                                char[] previousNameArray = previousName.toCharArray();
                                char[] nameArray = name.toCharArray();
         
                                if (nameArray[0] != previousNameArray[0]) {
                                    showSeparator = true;
                                }
         
                                cursor.moveToPosition(position);
                            }
         
                            // Cache it
                            mRowStates[position] = showSeparator ? SECTIONED_STATE : REGULAR_STATE;
         
                            break;
                    }
         
                    TextView separatorView = (TextView) view.findViewById(R.id.separator);
         
                    if (showSeparator) {
                        separatorView.setText(name.toCharArray(), 0, 1);
                        separatorView.setVisibility(View.VISIBLE);
                    }
                    else {
                        view.findViewById(R.id.separator).setVisibility(View.GONE);
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