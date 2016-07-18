package com.almexe.lingvaproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.almexe.lingvaproject.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainDbForUser{

    private static final String TAG = "MainDbForUser";
    private Context  mContext;
    private DbHelper  dbHelper;
    public SQLiteDatabase mDb;
    public static String   data;

    private static MainDbForUser sMainDbForUser = null;
    public static MainDbForUser getInstance() {
        if (sMainDbForUser == null) {
            sMainDbForUser = new MainDbForUser(Application.getContext());
        }
        return sMainDbForUser;
    }

    public static final String ID =            "_id";
    public static final String FOREGIN_WORD = "foregin_words";
    public static final String NATIV_WORD =    "nativ_words";
    public static final String TRANSCRIPTION =  "transcription";
    public static final String NUMBERS =  "mynumbers";
    public static final String DB_NAME =       "MainDbForUser";

    public static final String MAIN = "main_table";
    public static final String TEN =    "ten_table";
    public static final String OWN =  "own_table";
    public static final String LEARNED =  "learned_table";

    public static final int    SCHEMA_VERSION = 1;
    public static boolean rowExists = false;
    public String table;
    Cursor cursor;
    public int icount = 0;
    public String          foreginTenWords, nativTenWords, transTenWords;

    public String          foreginWord, nativWord, transcription;

    public List<String> notSortedListForForeignWords = new ArrayList<>();
    public List<String> notSortedListForNativWords = new ArrayList<>();
    public List<String> transArray = new ArrayList<>();

    public static List<String> notSortedListForForeign10Words = new ArrayList<>();
    public static List<String> notSortedListForNativ10Words = new ArrayList<>();
    public static List<String> trans10Array = new ArrayList<>();

    private class DbHelper extends SQLiteOpenHelper {

        protected DbHelper(Context context) {
            super(context, DB_NAME, null, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
            createTable(table);
        }
    }

    public MainDbForUser(Context context) {
        mContext = context;
    }

    public void copy(){
        String selectQuery2 = "SELECT * FROM words WHERE _id BETWEEN 0 AND 2000";
        MainDb mainDb = new MainDb(mContext);
        mDb = mainDb.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(selectQuery2, null);
        if(cursor.moveToFirst()) {
            do{
                String trans = cursor.getString(1);
                String foregin_word = cursor.getString(2);
                String nativ_word = cursor.getString(3);

                notSortedListForForeignWords.add(foregin_word);
                notSortedListForNativWords.add(nativ_word);
                transArray.add(trans);

            }while(cursor.moveToNext());
            rowExists = true;
        }else{
            rowExists = false;
        }

        cursor.close();
    }

    public void createTableTenWords(String table){
        mDb.execSQL("CREATE TABLE "+table+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+FOREGIN_WORD+" VARCHAR(200), " +
                ""+NATIV_WORD+" VARCHAR(200), " +
                ""+TRANSCRIPTION+" VARCHAR(200)" +
                ")");
    }

    public void createTable(String table){
        this.table = table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mDb.execSQL("CREATE TABLE "+table+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+TEN+" INTEGER, " +
                ""+OWN+" INTEGER, " +
                ""+LEARNED+" INTEGER" +
                ")");
    }

    public void createTableOwnWords(String table){
        mDb.execSQL("CREATE TABLE "+table+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+FOREGIN_WORD+" VARCHAR(200), " +
                ""+NATIV_WORD+" VARCHAR(200), " +
                ""+TRANSCRIPTION+" VARCHAR(200)" +
                ")");
    }

    public void createTableLearnedWords(String table){
        mDb.execSQL("CREATE TABLE "+table+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+FOREGIN_WORD+" VARCHAR(200), " +
                ""+NATIV_WORD+" VARCHAR(200), " +
                ""+TRANSCRIPTION+" VARCHAR(200)" +
                ")");
    }

    public void createTableForNumbers(String table){
        mDb.execSQL("CREATE TABLE "+table+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+NUMBERS+" INTEGER" +
                ")");
    }

    public void insert(String table){
        this.table = table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO "+ table +" VALUES (?,?,?,?);";
        SQLiteStatement statement = mDb.compileStatement(sql);
        mDb.beginTransaction();
        for(int i = 1; i < 2001; i++){
            statement.clearBindings();
            statement.bindLong(1, i);
            statement.bindLong(4, 0);
            statement.execute();
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    public void insertLearned(String table){
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO "+ table +" VALUES (?,?,?,?);";
        SQLiteStatement statement = mDb.compileStatement(sql);
        mDb.beginTransaction();
        for(int i = 1; i < 2001; i++){
            statement.clearBindings();
            statement.bindNull(2);
            statement.execute();
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    public void update(String table, String column, int id){
        dbHelper  = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        mDb.execSQL("UPDATE "+table+" SET "+column+" = '1' WHERE "+ID+" = "+id+"");
    }

    public void updateToNull(String table, String column, int id){
        dbHelper  = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        mDb.execSQL("UPDATE "+table+" SET "+column+" = '0' WHERE "+ID+" = "+id+"");
    }

    public void updateToNull(String table, String column){
        dbHelper  = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        mDb.execSQL("UPDATE "+table+" SET "+column+" = '0' WHERE "+column+" = '1'");
    }

    public void deleteFromTableWhereColumnEqualsOne(String table, String column){
        dbHelper  = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        mDb.execSQL("DELETE FROM "+table+" WHERE "+column+" = '1'");
    }

    public void write(String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FOREGIN_WORD, foreginWord);
        values.put(NATIV_WORD, nativWord);
        values.put(TRANSCRIPTION, transcription);
        mDb.insert(table, null, values);
        mDb.close();
    }

    public void writeNumber(int number, String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO "+ table +" VALUES (?,?);";
        SQLiteStatement statement = mDb.compileStatement(sql);
        mDb.beginTransaction();
        statement.clearBindings();
        statement.bindLong(2, number);
        statement.execute();
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();
    }

    public String getWord(int position, int column, String table){
        String selectQuery = "SELECT * FROM " + table;
        dbHelper  = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String result;
        cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(position);
        result = cursor.getString(column);
        cursor.close();
        return result;
    }

    public int getNotLearnedWords(int position, String table){
        String selectQuery = "SELECT * FROM " + table + " WHERE " + LEARNED + " = '0' ORDER BY RANDOM()";
        dbHelper  = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        int result;
        cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToPosition(position);
        result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    public void readNumber(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do{
                String number = cursor.getString(1);

                notSortedListForForeignWords.add(number);
                rowExists = true;

            }while(cursor.moveToNext());
        }else{
            rowExists = false;
        }
        cursor.close();
        mDb.close();
    }

    public int getCountLessonWordsFromTen (String table, String column) {
        String selectQuery = "SELECT COUNT("+column+") FROM " + table + " WHERE " + column + " = '1' ";
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        int count = 0;
        if(null != cursor)
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
        cursor.close();
        mDb.close();
        return count;
    }

    public ArrayList<Integer> getListId(String table, String column) {
        String selectQuery = "SELECT _id FROM " + table + " WHERE " + column + " = '1' ";
        dbHelper = new DbHelper(mContext);
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

    public int isLast2(String table) {
        int position;
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        cursor.moveToLast();

        position = cursor.getPosition();

        cursor.close();
        return position;
    }

    public void readTen(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        cursor = mDb.rawQuery(selectQuery, null);
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
    }

    public void writeTen(String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FOREGIN_WORD, foreginTenWords);
        values.put(NATIV_WORD, nativTenWords);
        values.put(TRANSCRIPTION, transTenWords);
        mDb.insert(table, null, values);
        mDb.close();
    }

    public void delete(String words, String table, int id) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mDb.delete(table, FOREGIN_WORD + " = ?", new String[] { words });
        mDb.delete(table, NATIV_WORD + " = ?", new String[] { words });
        mDb.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '"+table+"'");
        mDb.close();
    }

    public void delete(String words, String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mDb.delete(table, FOREGIN_WORD + " = ?", new String[] { words });
        mDb.delete(table, NATIV_WORD + " = ?", new String[] { words });
        mDb.close();
    }

    public void delete(int id, String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mDb.delete(table, NUMBERS + " = ?", new String[] {String.valueOf(id)});
        mDb.close();
    }

    public String readNowForegin(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readForegin;
        cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(icount);
        readForegin = cursor.getString(1);

        cursor.close();
        mDb.close();
        return readForegin;
    }

    public String readNowNative(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readNative;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(icount);
        readNative = cursor.getString(2);

        cursor.close();
        mDb.close();
        return readNative;
    }

    public String readNowTrans(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readTrans;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToPosition(icount);
        readTrans = cursor.getString(3);

        cursor.close();
        mDb.close();
        return readTrans;
    }

    public int getCount(String table){
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        int result;
        cursor = mDb.rawQuery(selectQuery, null);
        result = cursor.getCount();
        return result;
    }

    public boolean isExists(String table) {
        try {
            dbHelper = new DbHelper(mContext);
            mDb = dbHelper.getWritableDatabase();
            mDb.rawQuery("SELECT * FROM " + table, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void deleteDb(String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mDb.execSQL("DROP TABLE IF EXISTS " + table);
        mDb.execSQL("CREATE TABLE "+table+" (" +
                ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+FOREGIN_WORD+" VARCHAR(200), " +
                ""+NATIV_WORD+" VARCHAR(200), " +
                ""+TRANSCRIPTION+" VARCHAR(200)" +
                ")");
        mDb.close();
    }

    public boolean isLast(String table){
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result;
        cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToPosition(icount);
        result = cursor.isLast();
        Log.i(TAG, "isLast" + String.valueOf(cursor.isLast()));
        return result;
    }

    public String readNextForegin(String table) {

        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        cursor = mDb.rawQuery(selectQuery, null);
        String readForegin;

        icount++;
        cursor.moveToPosition(icount);
        readForegin = cursor.getString(1);

        cursor.close();
        mDb.close();
        return readForegin;
    }

    public String readPreviousForegin(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        String readForegin;
        cursor = mDb.rawQuery(selectQuery, null);

        icount--;
        cursor.moveToPosition(icount);
        readForegin = cursor.getString(1);

        cursor.close();
        mDb.close();

        return readForegin;
    }

    public boolean isFirst(String table){
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result;
        cursor = mDb.rawQuery(selectQuery, null);
        cursor.moveToPosition(icount);
        result = cursor.isFirst();
        Log.i("in method isLast = ", String.valueOf(cursor.isLast()));
        return result;
    }

    public boolean isRowExists(String word, String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                String foregin_word10 = cursor.getString(1);

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

    public int getIdForeginWord(String word, String table){
        String selectQuery = "SELECT _id FROM " + table + " WHERE " + FOREGIN_WORD + " = '" + word + "' ";
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        int id;
        cursor = mDb.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        id = cursor.getInt(0);

        cursor.close();
        mDb.close();
        return id;
    }

    public int getIdNativeWord(String word, String table){
        String selectQuery = "SELECT _id FROM " + table + " WHERE " + NATIV_WORD + " = '" + word + "' ";
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

    public int getNativWord(String word, String table){
        String selectQuery = "SELECT _id FROM " + table + " WHERE " + NATIV_WORD + " = '" + word + "' ";
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

    public void writeOwnWords(String table) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FOREGIN_WORD, foreginWord);
        values.put(NATIV_WORD, nativWord);
        values.put(TRANSCRIPTION, transcription);
        mDb.insert(table, null, values);
        mDb.close();
    }

    public ArrayList<String> listWords(String table) {
        String selectQuery = "SELECT * FROM " + table;
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

    public ArrayList<String> listNativeWords(String table) {
        String selectQuery = "SELECT * FROM " + table;
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                list.add(cursor.getString(2));
            }while(cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
        return list;
    }

}
