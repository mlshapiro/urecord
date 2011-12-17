package cc.co.eurdev.eurecorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {
	
	public static final String KEY_ROW_ID = "_id";
    public static final String COL_TYPE = "type";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_TIME_CREATED = "time_created";
    public static final String COL_LENGTH = "length";
    public static final String COL_PATH = "path";
    private static final String DATABASE_TABLE = "files";
    
    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    
    public DBAdapter(Context ctx) {
            this.context = ctx;
    }
    
    // opens the database
    public DBAdapter open() throws SQLException {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            return this;
    }
    
    // closes the database
    public void close() {
            dbHelper.close();
    }
    
    // insert entry into the database
    public long addEntry(String id, String type, String create_date, String create_time, String length, String path) {
            Log.i(DBAdapter.class.getName(), "Inserting record...");
            
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_ROW_ID, id);
            initialValues.put(COL_TYPE, type);
            initialValues.put(COL_DATE_CREATED, create_date);
            initialValues.put(COL_TIME_CREATED, create_time);
            initialValues.put(COL_LENGTH, length);
            initialValues.put(COL_PATH, path);
            Log.i(DBAdapter.class.getName(), "before db insert");
            long result = 0;
            try {
                    result = db.insert(DATABASE_TABLE, null, initialValues);
            } catch (Exception e) {
                    Log.e(DBAdapter.class.getName(), "Problem: " + e);
            }
            
            return result;
    }
    
    // remove entry from the database
    public boolean deleteEntry(String id) {
            return db.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + id, null) > 0;
    }
    
    public void deleteAll() {
    	db.delete(DATABASE_TABLE, null, null);
    }
    
    // get all "expenses" entries
//  public Cursor getExpenseEntries() {
//          String query = "SELECT _id, name, amount, date " +
//                                          "FROM entries " +
//                                          "WHERE type = ?";
//          return db.rawQuery(query, new String[] { "expense" });
//  }
//    public Cursor getExpenseEntries() {
//            return db.query(DATABASE_TABLE, new String[] {
//                            KEY_ROWID, COL_TYPE, COL_DATE_CREATED, COL_TIME_CREATED, COL_LENGTH }, 
//                            COL_TYPE + "='expense'", null, null, null, null);
//    }
    
    // get all "income" entries
    /*public Cursor getIncomeEntries() {
            String query = "SELECT _id, name, amount, date " +
                                            "FROM entries " +
                                            "WHERE type = ?";
            return db.rawQuery(query, new String[] { "income" });
    }*/
//    public Cursor getIncomeEntries() {
//            return db.query(DATABASE_TABLE, new String[] {
//                            KEY_ROWID, COL_TYPE, COL_DATE_CREATED, COL_TIME_CREATED, COL_LENGTH, }, 
//                            COL_TYPE + "='income'", null, null, null, null);
//    }
    
    public Cursor getEntries() {
    	return db.query(DATABASE_TABLE, new String[] {
                KEY_ROW_ID, COL_TYPE, COL_DATE_CREATED, COL_TIME_CREATED, COL_LENGTH, COL_PATH }, null, null, null, null, null);
    }
    
    public Cursor getEntriesOrderById() {
    	return db.query(DATABASE_TABLE, new String[] {
                KEY_ROW_ID, COL_TYPE, COL_DATE_CREATED, COL_TIME_CREATED, COL_LENGTH, COL_PATH }, null, null, null, null, KEY_ROW_ID + " DESC");
    }
    
    public Cursor getEntryPathById(String id) {
    	return db.query(DATABASE_TABLE, new String[] { COL_PATH }, KEY_ROW_ID +"="+id, null, null, null, null);
    }
    
    // get a particular entry
    public Cursor getEntry(long rowID) throws SQLException {
            Cursor c =
                    db.query(true, DATABASE_TABLE, new String[] {
                                    KEY_ROW_ID, COL_DATE_CREATED, COL_TIME_CREATED, COL_LENGTH, COL_PATH }, 
                                    KEY_ROW_ID +"="+rowID, null, null, null, null, null);
            
            if (c != null) {
                    c.moveToFirst();
            }
            return c;
    }
    
//    public boolean updateRowId(int rowID, int newRowId) {
//        ContentValues args = new ContentValues();
//        args.put(KEY_ROW_ID, newRowId);
//        return db.update(DATABASE_TABLE, args, KEY_ROW_ID+"="+rowID, null) > 0;
//    }
    
    // update an entry
    public boolean updateEntry(long rowID, String type, String create_date, String create_time, String length, String path) {
            ContentValues args = new ContentValues();
            args.put(COL_TYPE, type);
            args.put(COL_DATE_CREATED, create_date);
            args.put(COL_TIME_CREATED, create_time);
            args.put(COL_LENGTH, length);
            args.put(COL_PATH, path);
            
            return db.update(DATABASE_TABLE, args, KEY_ROW_ID+"="+rowID, null) > 0;
    }

}
