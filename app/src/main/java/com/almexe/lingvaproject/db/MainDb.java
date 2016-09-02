package com.almexe.lingvaproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainDb extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/ru.lingva/databases/";
    private static String DB_NAME = "MainDb";
    private SQLiteDatabase myDataBase;
    private final Context mContext;
    static Context context;
    MainDb helper;
    public static boolean rowExists = false;

    public static final String FOREGIN_WORD = "foregin_words";
    public static final String NATIV_WORD = "nativ_words";
    public static final String TRANS = "translit";
    public static final String TABLE = "words";
    public static final String ID = "_id";

    Cursor cursor;

    public int icount = 0;

    public List<String> notSortedListForForeignWords = new ArrayList<>();
    public List<String> notSortedListForNativWords = new ArrayList<>();
    public List<String> transArray = new ArrayList<>();

    public String foreginWord, nativWord;

    public MainDb(Context context) {
        super(context, DB_NAME, null, 1);
        if(android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if(dbExist) {
            //do nothing
        } else {
            //copy database
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {
//Open stream and get db
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
//Get Stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
//
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void write() {
        helper = new MainDb(mContext);
        myDataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FOREGIN_WORD, foreginWord);
        values.put(NATIV_WORD, nativWord);
        myDataBase.insert(TABLE, null, values);
        myDataBase.close();
    }

    public void read() {
        String selectQuery = "SELECT * FROM " + TABLE;
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                String trans = cursor.getString(1);
                String foregin_word = cursor.getString(2);
                String nativ_word = cursor.getString(3);

                notSortedListForForeignWords.add(foregin_word);
                notSortedListForNativWords.add(nativ_word);
                transArray.add(trans);
                rowExists = true;

            } while (cursor.moveToNext());
        } else {
            rowExists = false;
        }
        cursor.close();
        myDataBase.close();
    }

    public int getWord(String word) {
        String selectQuery = "SELECT _id FROM " + TABLE + " WHERE foregin_words = '" + word + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        int id;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        id = cursor.getInt(0);
        cursor.close();
        myDataBase.close();
        return id;
    }

    public String getNativeWord(String word) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + NATIV_WORD + " = '" + word + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String result = null;
        cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        try {
            result = cursor.getString(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        myDataBase.close();
        return result;
    }

    public String getNativeWordByForeign(String foregnWord) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + FOREGIN_WORD + " = '" + foregnWord + "'";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String result;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        result = cursor.getString(3);
        cursor.close();
        return result;
    }

    public String getWordById(int id) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + id + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        cursor = myDataBase.rawQuery(selectQuery, null);
        String result;
        cursor.moveToFirst();

        result = cursor.getString(2);

        cursor.close();
        myDataBase.close();
        return result;
    }

    public String getTranslateById(int id) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + id + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        cursor = myDataBase.rawQuery(selectQuery, null);
        String result;
        cursor.moveToFirst();
        result = cursor.getString(1);

        cursor.close();
        myDataBase.close();
        return result;
    }

    public String getForeinWordById(int id) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + id + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        cursor = myDataBase.rawQuery(selectQuery, null);
        String result;
        cursor.moveToFirst();
        result = cursor.getString(2);

        cursor.close();
        myDataBase.close();
        return result;
    }

    public String getNativeWordById(int id) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + id + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        cursor = myDataBase.rawQuery(selectQuery, null);
        String result;
        cursor.moveToFirst();
        result = cursor.getString(3);

        cursor.close();
        myDataBase.close();
        return result;
    }

    public String readNextForegin(int position) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + position + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        cursor = myDataBase.rawQuery(selectQuery, null);
        String readForegin;
//icount++;
        cursor.moveToFirst();
        readForegin = cursor.getString(2);

        cursor.close();
        myDataBase.close();
        return readForegin;
    }

    public String readNowTrans(int position) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + position + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String readTrans;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        readTrans = cursor.getString(1);

        cursor.close();
        myDataBase.close();
        return readTrans;
    }

    public String readPreviousForegin(int position) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + position + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String readForegin;
        cursor = myDataBase.rawQuery(selectQuery, null);

//icount--;
        cursor.moveToFirst();
        readForegin = cursor.getString(2);

        cursor.close();
        myDataBase.close();

        return readForegin;
    }

    public String readNowNative(int position) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + position + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String readNative;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        readNative = cursor.getString(3);

        cursor.close();
        myDataBase.close();
        return readNative;
    }

    public String readNowForegin(int position) {
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + ID + " = '" + position + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String readForegin;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        readForegin = cursor.getString(2);

        cursor.close();
        myDataBase.close();
        return readForegin;
    }

    public boolean isRowExists(String word) {
        String selectQuery = "SELECT * FROM " + TABLE;
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                String foregin_word10 = cursor.getString(2);

                if(foregin_word10.equals(word)) {
                    result = true;
                }

            } while (cursor.moveToNext());
            rowExists = true;
        } else {
            rowExists = false;
        }
        cursor.close();
        myDataBase.close();
        return result;
    }

    public String getWord(int position, int column) {
        String selectQuery = "SELECT * FROM " + TABLE;
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String result;
        cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToPosition(position);
        result = cursor.getString(column);
        return result;
    }

    public int getIdForeginWord(String word) {
        String selectQuery = "SELECT _id FROM " + TABLE + " WHERE " + FOREGIN_WORD + " = '" + word + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        int id;
        cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        id = cursor.getInt(0);

        cursor.close();
        myDataBase.close();
        return id;
    }

    public int getIdNativeWord(String word) {
        String selectQuery = "SELECT _id FROM " + TABLE + " WHERE " + NATIV_WORD + " = '" + word + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        int id;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        id = cursor.getInt(0);
        cursor.close();
        myDataBase.close();
        return id;
    }

    public ArrayList<String> listWords(String table, String id) {
        String selectQuery = "SELECT * FROM " + table + " WHERE " + ID + " = '" + id + "'";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();
        myDataBase.close();
        return list;
    }

    public boolean isEmpty() {
        boolean empty = true;
        Cursor cur = myDataBase.rawQuery("SELECT COUNT(*) FROM YOURTABLE", null);
        if(cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        return empty;
    }
}