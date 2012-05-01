//This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package cc.co.eurdev.urecorder.db;

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
