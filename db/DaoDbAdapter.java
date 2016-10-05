package com.itgapps.appidol.db;

import java.io.IOException; 

import android.content.Context; 
import android.database.Cursor; 
import android.database.SQLException; 
import android.database.sqlite.SQLiteDatabase; 
import android.util.Log; 
 
public class DaoDbAdapter  
{ 
    protected static final String TAG = "DataAdapter"; 
 
    private final Context mContext; 
    private SQLiteDatabase mDb; 
    private DataBaseHelper mDbHelper; 
 
    public DaoDbAdapter(Context context)  
    { 
        this.mContext = context; 
        mDbHelper = new DataBaseHelper(mContext); 
    } 
 
    public DaoDbAdapter createDatabase() throws SQLException  
    { 
        try  
        { 
            mDbHelper.createDataBase(); 
        }  
        catch (IOException mIOException)  
        { 
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase"); 
            throw new Error("UnableToCreateDatabase"); 
        } 
        return this; 
    } 
 
    public DaoDbAdapter open() throws SQLException  
    { 
        try  
        { 
            mDbHelper.openDataBase(); 
            mDbHelper.close(); 
            mDb = mDbHelper.getReadableDatabase(); 
        }  
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "open >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
        return this; 
    } 
 
    public void close()  
    { 
        mDbHelper.close(); 
    } 
 
    
    
    //Metods DAO
    
    // get All Albums
    public Cursor getAlbums() { 
         try 
         { 
             String sql ="SELECT * FROM albums"; 
 
             Cursor mCur = mDb.rawQuery(sql, null); 
             if (mCur!=null) 
             { 
                mCur.moveToNext(); 
             } 
             return mCur; 
         } 
         catch (SQLException mSQLException)  
         { 
             Log.e(TAG, "getAlbums >> "+ mSQLException.toString()); 
             throw mSQLException; 
         } 
     }
    
    // get Album
    // params: albumId
    public Cursor getTracks(String albumId){   
        try 
        { 
            String sql ="SELECT * FROM tracks WHERE albums_id = " + albumId; 

            Cursor mCur = mDb.rawQuery(sql, null); 
            if (mCur!=null) 
            { 
               mCur.moveToNext(); 
            } 
            return mCur; 
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getAlbums >> "+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
    
    // get Lyrics
    // params: trackId
    public Cursor getLyric(String trackId){   
        try 
        { 
            String sql ="SELECT ly.*, tr.*" +
            		" FROM lyrics AS ly" +
            		" INNER JOIN tracks tr ON ly.tracks_id = tr._id" +
            		//Remove when production
            		//" WHERE tracks_id = " + trackId; 
            		" WHERE tracks_id = " + 1; 

            Cursor mCur = mDb.rawQuery(sql, null); 
            if (mCur!=null) 
            { 
               mCur.moveToNext(); 
            } 
            return mCur; 
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getLyric >> "+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
    
    // get All Lyrics
    public Cursor getAllLyrics(){   
        try 
        { 
            String sql ="SELECT *" +
            		" FROM tracks" + 
            		" ORDER BY name ASC"; 

            Cursor mCur = mDb.rawQuery(sql, null); 
            if (mCur!=null) 
            { 
               mCur.moveToNext(); 
            } 
            return mCur; 
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getLyric >> "+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
    
    public Cursor getSong(String trackId){   
        try 
        {         	
            String sql ="SELECT tr.*, al.name as album, al.cover as cover" +
            		" FROM tracks AS tr" +
            		" INNER JOIN albums al ON tr.albums_id = al._id" +
            		" WHERE tr._id = " + trackId; 

            Cursor mCur = mDb.rawQuery(sql, null); 
            if (mCur!=null) 
            { 
               mCur.moveToNext(); 
            } 
            return mCur; 
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getSong >> "+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
    
    // get Song
    // params: trackId
    // old
    /*
    public Cursor getSong(String trackId){   
        try 
        { 
            String sql ="SELECT *" +
            		" FROM tracks " +
            		" WHERE _id = " + trackId; 

            Cursor mCur = mDb.rawQuery(sql, null); 
            if (mCur!=null) 
            { 
               mCur.moveToNext(); 
            } 
            return mCur; 
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getSong >> "+ mSQLException.toString()); 
            throw mSQLException; 
        }     
    }
    */
    
    
 // get All Videos
    public Cursor getVideos() { 
         try 
         { 
             String sql ="SELECT * FROM tracks WHERE urlVideo IS NOT NULL"; 
 
             Cursor mCur = mDb.rawQuery(sql, null); 
             if (mCur!=null) 
             { 
                mCur.moveToNext(); 
             } 
             return mCur; 
         } 
         catch (SQLException mSQLException)  
         { 
             Log.e(TAG, "getVideos >> "+ mSQLException.toString()); 
             throw mSQLException; 
         } 
     }
    /*
 	public boolean SaveEmployee(String name, String email) 
 	{
 		try
 		{
 			ContentValues cv = new ContentValues();
 			cv.put("Name", name);
 			cv.put("Email", email);
 			
 			mDb.insert("Employees", null, cv);

 			Log.d("SaveEmployee", "informationsaved");
 			return true;
 			
 		}
 		catch(Exception ex)
 		{
 			Log.d("SaveEmployee", ex.toString());
 			return false;
 		}
 	}
    */ 

} 

