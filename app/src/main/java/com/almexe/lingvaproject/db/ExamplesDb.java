package com.almexe.lingvaproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ExamplesDb {

    public static final String WORD = "word";
    public static final String EXAMPLE = "example";
    public static final String TABLE = "example_table";
    public static final String DB_NAME = "examples";
    public static final int SCHEMA_VERSION = 1;
    public static final String ID = "_id";

    private DbHelper dbHelper;
    private SQLiteDatabase mDb;
    private Cursor cursor;
    public static boolean rowExists = false;

    class DbHelper extends SQLiteOpenHelper {
        final String CREATE_TABLE = "CREATE TABLE "+TABLE+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+WORD+" CHAR(300), " +
                ""+EXAMPLE+" CHAR(300))";

        Context mContext;

        public DbHelper(Context context){
            super(context, DB_NAME, null, SCHEMA_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS example_table");
            onCreate(db);
        }
    }

    public ExamplesDb(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void createTable(){;
        mDb = dbHelper.getWritableDatabase();
        mDb.execSQL("CREATE TABLE "+TABLE+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+WORD+" CHAR(300), " +
                ""+EXAMPLE+" CHAR(300))");
    }

    public void write(String example, String word) {
        mDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WORD, word);
        values.put(EXAMPLE, example);
        mDb.insert(TABLE, null, values);
        mDb.close();
    }

    public String getWords(String word, int position){
        mDb = dbHelper.getReadableDatabase();
        String result;
        cursor = mDb.rawQuery("SELECT "+EXAMPLE+" FROM " + TABLE + " WHERE " + WORD +" =?", new String[]{word});
        cursor.moveToPosition(position);
        result = cursor.getString(0);
        cursor.close();
        return result;
    }

    public String getWord(String word){
        mDb = dbHelper.getReadableDatabase();
        String result;
        cursor = mDb.rawQuery("SELECT "+EXAMPLE+" FROM " + TABLE + " WHERE " + WORD +" =?", new String[]{word});
        cursor.moveToPosition(0);
        result = cursor.getString(0);
        cursor.close();
        return result;
    }

    public long fetchPlacesCount(String word) {
        mDb = dbHelper.getReadableDatabase();
        cursor = mDb.rawQuery("SELECT COUNT(_id) FROM " + TABLE +" WHERE "+WORD+" =?" , new String[]{word});
        cursor.moveToFirst();
        int i = cursor.getInt(0);
        cursor.close();
        return i;
    }

    public ArrayList<Integer> getListId(String column) {
        String selectQuery = "SELECT _id FROM " + TABLE + " WHERE " + column + " = '1' ";
        mDb = dbHelper.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();
        cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                list.add(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
        return list;
    }

    public boolean isRowExists(String word) {
        String selectQuery = "SELECT * FROM " + TABLE;
        mDb = dbHelper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                String fetch_word = cursor.getString(1);
                if(fetch_word.equals(word)) result = true;
            }while(cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
        return result;
    }
}
