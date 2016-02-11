package com.almexe.lingvaproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbTenWords {
	
	private Context        mContext;
	private DbHelper       dbHelper;
	public SQLiteDatabase mDb;
	public String          foreginTenWords, nativTenWords, transTenWords;
	public static String   data;

	public static final String FOREGIN_WORD10 = "foregin_words_10";
	public static final String NATIV_WORD10 =    "nativ_words_10";
	public static final String TRANSCRIPTION10 =  "transcription_10";
	public static final String TABLE =         "tenwords";
	public static final String DB_NAME =       "DbTenWords";
	public static final int    SCHEMA_VERSION = 1;
	public static boolean rowExists = false;
    Cursor cursor;
    public int icount = 0;
	
	public static List<String> notSortedListForForeign10Words = new ArrayList<>();
	public static List<String> notSortedListForNativ10Words = new ArrayList<>();
	public static List<String> trans10Array = new ArrayList<>();

	public static final String SQL_CREATE_TABLE = 
			"CREATE TABLE tenwords (" + 
				    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				    "transcription_10 VARCHAR(200), " + 
					"foregin_words_10 VARCHAR(200), " + 
					"nativ_words_10 VARCHAR(200)" + 
					")";
	public static final String SQL_DROP_TABLE = 
			"DROP TABLE IF EXISTS tenwords";

	private final class DbHelper extends SQLiteOpenHelper {

		protected DbHelper(Context context) {
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
	
	public DbTenWords(Context context) {
		mContext = context;
	}
	
	/*public void close() {
	    if (mDb != null) {
	    	mDb.close();
	    }
	}*/

    public void deleteDb() {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mDb.execSQL(SQL_DROP_TABLE);
        mDb.execSQL(SQL_CREATE_TABLE);
    }
	
	public void writeTen() {
		dbHelper = new DbHelper(mContext);
		mDb = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FOREGIN_WORD10, foreginTenWords);
		values.put(NATIV_WORD10, nativTenWords);
		values.put(TRANSCRIPTION10, transTenWords);
		mDb.insert(TABLE, null, values);
		mDb.close();
	}
	
	public void readTen() {
		String selectQuery = "SELECT * FROM " + TABLE;
		dbHelper = new DbHelper(mContext);
		mDb = dbHelper.getReadableDatabase();
		Cursor cursor = mDb.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()) {
			do{
				String trans10 = cursor.getString(1);
				String foregin_word10 = cursor.getString(2);
				String nativ_word10 = cursor.getString(3);
				
				trans10Array.add(trans10);
				notSortedListForForeign10Words.add(foregin_word10);
				notSortedListForNativ10Words.add(nativ_word10);

			}while(cursor.moveToNext());	
			rowExists = true;
		}else{
			rowExists = false;
		}
		cursor.close();
		mDb.close();
	}

    public boolean isRowExists(String word) {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                String foregin_word10 = cursor.getString(2);

                if(foregin_word10.equals(word)){
                    result = true;
                }

            }while(cursor.moveToNext());
            rowExists = true;
        }else{
            rowExists = false;
        }
        cursor.close();
        mDb.close();
        return result;
    }
	
	/*public boolean isEmpty() {
		boolean empty = true;
		Cursor cur = mDb.rawQuery("SELECT COUNT(*) FROM tenwords", null);
		if (cur != null && cur.moveToFirst()) {
		    empty = (cur.getInt (0) == 0);
		}
		cur.close();

		return empty;
	}*/

    public String readNowForegin() {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readForegin;
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
        String readNative;
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
        String readTrans;
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

        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(position);

        result = cursor.getString(column);

        cursor.close();
        mDb.close();
        return result;
    }

    public int getNativWord(String word){
        String selectQuery = "SELECT _id FROM " + TABLE + " WHERE nativ_words_10 = '" + word + "' ";
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase mDb = dbHelper.getReadableDatabase();
        int id;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        id = cursor.getInt(0);

        cursor.close();
        mDb.close();
        return id;
    }

    public int getIdForeginWord(String word){
        String selectQuery = "SELECT _id FROM " + TABLE + " WHERE foregin_words_10 = '" + word + "' ";
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        int id;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        id = cursor.getInt(0);

        cursor.close();
        mDb.close();
        return id;
    }

    public int getCount(){
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        int result;
        cursor = mDb.rawQuery(selectQuery, null);
        Log.i("result = ", String.valueOf(cursor.getCount()));
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
        String readForegin;

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
        String readForegin;
        cursor = mDb.rawQuery(selectQuery, null);

        icount--;
        cursor.moveToPosition(icount);
        readForegin = cursor.getString(2);

        cursor.close();
        mDb.close();

        return readForegin;
    }

    public ArrayList<String> listWords() {
        String selectQuery = "SELECT * FROM " + TABLE;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
            list.add(cursor.getString(3));
            }while(cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
        return list;
    }

	public int isLast2() {
		int position;
		String selectQuery = "SELECT * FROM " + TABLE;
		dbHelper = new DbHelper(mContext);
		mDb = dbHelper.getReadableDatabase();
		Cursor cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        cursor.moveToLast();

		position = cursor.getPosition();

        cursor.close();
        mDb.close();
		return position;
		}

	/*public void delete() {
		String words = LessonTenWordFragment.mainDataTextView.getText().toString();

		dbHelper = new DbHelper(mContext);
		mDb = dbHelper.getWritableDatabase();
		mDb.delete(TABLE, "foregin_words_10" + " = ?", new String[] {  words });
		Log.i("delete=", "foregin_words_10"+" = "+words);
	
	}*/
	
}