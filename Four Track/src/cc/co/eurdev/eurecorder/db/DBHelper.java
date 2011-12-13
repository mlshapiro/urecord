package cc.co.eurdev.eurecorder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eurecorddata";
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_CREATE =
            "CREATE TABLE files (_id TEXT PRIMARY KEY, "
            + "type TEXT NOT NULL, " +
                            "date_created TEXT NOT NULL, " +
                            "time_created TEXT NOT NULL, " +
                            "length TEXT NOT NULL," +
                            "path TEXT NOT NULL);";
    
    public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DBHelper.class.getName(), 
                            "Upgrading database from version " + oldVersion
                            + " to " + newVersion + ", " +
                            "which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS entries");
            onCreate(db);

    }

}
