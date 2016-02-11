package com.almexe.lingvaproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.almexe.lingvaproject.pages.OwnLessonFragment;

public class UserDb {

    private Context mContext;
    private DbHelper       dbHelper;
    private SQLiteDatabase mDb;

    public static int   user_id;
    public static final String ID =            "_id";
    public static final String USER_ID =       "user_id";
    public static final String TABLE =         "User";
    public static final String DB_NAME =       "User";
    public static final int  SCHEMA_VERSION = 1;

    public static boolean rowExists = false;
    private int icount = 0;

    Cursor cursor;

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE + " (" +
                    ""+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ""+ USER_ID +" INTEGER NOT NULL)";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS "+ TABLE +"";

    private final class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, SCHEMA_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DROP_TABLE);
            onCreate(db);
        }
    }
    public UserDb(Context context) {
        mContext = context;
    }

    public void close() {
        if (mDb != null) {
            mDb.close();
        }
    }

    public void write() {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID, user_id);
        mDb.insert(TABLE, null, values);
        mDb.close();
    }

    public boolean isRowExists(int user_id) {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                int userId = cursor.getInt(1);
                Log.e("userId", String.valueOf(userId));
                if(userId == user_id){
                    result = true;
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
        return result;
    }

    public String readNowForegin() {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readForegin = null;
        cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(icount);
        readForegin = cursor.getString(2);

        cursor.close();
        mDb.close();
        return readForegin;
    }

    public String readNowNative() {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readNative= null;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(icount);
        readNative = cursor.getString(3);

        cursor.close();
        mDb.close();
        return readNative;
    }

    public String readNowTrans() {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readTrans= null;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(icount);
        readTrans = cursor.getString(1);

        cursor.close();
        mDb.close();
        return readTrans;
    }

    public boolean isLast(){
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result;
        cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToPosition(icount);
        result = cursor.isLast();
        Log.i("in method isLast = ", String.valueOf(cursor.isLast()));
        return result;
    }

    public String getWord(int position, int column){
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String result;
        cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(position);
        result = cursor.getString(column);
        return result;
    }

    public int getCount(){
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        int result;
        cursor = mDb.rawQuery(selectQuery, null);
        result = cursor.getCount();
        return result;
    }

    public boolean isFirst(){
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result;
        cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToPosition(icount);
        result = cursor.isFirst();
        Log.i("in method isLast = ", String.valueOf(cursor.isLast()));
        return result;
    }

    public String readNextForegin() {

        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        cursor = mDb.rawQuery(selectQuery, null);
        String readForegin = null;

        icount++;
        cursor.moveToPosition(icount);
        readForegin = cursor.getString(2);

        cursor.close();
        mDb.close();
        return readForegin;
    }

    public String readPreviousForegin() {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readForegin = null;
        cursor = mDb.rawQuery(selectQuery, null);

        icount--;
        cursor.moveToPosition(icount);
        readForegin = cursor.getString(2);

        cursor.close();
        mDb.close();

        return readForegin;
    }

    public boolean isEmpty() {
        boolean empty = true;
        Cursor cur = mDb.rawQuery("SELECT COUNT(*) FROM ownwords", null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        cur.close();

        return empty;
    }

    public void delete(String words) {

        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        String DataFromTextView = String.valueOf(OwnLessonFragment.mainDataTextView.getText());
        mDb.delete(TABLE, "foregin_words" + " = ?", new String[] { words });
        mDb.delete(TABLE, "nativ_words" + " = ?", new String[] { words });
        Log.i("delete=", "foregin_words"+" = "+DataFromTextView);

    }
}
