package com.almexe.lingvaproject.db;

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

public class SupplementDb extends SQLiteOpenHelper{

	private static String DB_PATH = "/data/data/ru.lingva/databases/";
    private static String DB_NAME = "SupplementDb";
    private SQLiteDatabase myDataBase;
    private final Context mContext;
    MainDb helper;
	
	public static final String FOREGIN_WORD = "foregin_words";
	public static final String NATIV_WORD =    "nativ_words";
	public static final String TRANS =    		"translit";
	public static final String TABLE =         "words";
	
	public static List<String> notSortedListForForeignWordsSupp = new ArrayList<String>();
	public static List<String> notSortedListForNativWordsSupp = new ArrayList<String>();
	public static List<String> transArraySupp = new ArrayList<String>();
	
	public String foreginWord, nativWord;
	
	public SupplementDb(Context context) {
		super(context, DB_NAME, null, 1);
		if(android.os.Build.VERSION.SDK_INT >= 4.2){
	        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
	    } else {
	       DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	    }
    this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase();

    	if(dbExist){
    		//do nothing
    	}else{
    		//copy database
        	this.getReadableDatabase();
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
    }

    public String getWord(int position, int column){
        String selectQuery = "SELECT * FROM " + TABLE;
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        String result;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToPosition(position);
        result = cursor.getString(column);
        return result;
    }

    public int getWord(String word){
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE foregin_words = '" + word + "' ";
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        int id = 0;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        id = cursor.getInt(1);

        return id;
    }

    public boolean isRowExists(String word) {
        String selectQuery = "SELECT * FROM " + TABLE;
        helper = new MainDb(mContext);
        myDataBase = helper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                String foregin_word10 = cursor.getString(2);

                if(foregin_word10.equals(word)){
                    result = true;
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
        myDataBase.close();
        return result;
    }

    private boolean checkDataBase(){
    	File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException{
    	//Open stream and get db
    	InputStream myInput = mContext.getAssets().open(DB_NAME);
    	String outFileName = DB_PATH + DB_NAME;
    	//Get Stream
    	OutputStream myOutput = new FileOutputStream(outFileName);

    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}

    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }

    public void openDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
	public synchronized void close() {
    	    if(myDataBase != null)
    		    myDataBase.close();
    	    super.close();
	}
    
    public void read() {
		String selectQuery = "SELECT * FROM " + TABLE;
		helper = new MainDb(mContext);
		myDataBase = helper.getReadableDatabase();
		Cursor cursor = myDataBase.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()) {
			do{
				String trans = cursor.getString(1);
				String foregin_word = cursor.getString(2);
				String nativ_word = cursor.getString(3);
				
				notSortedListForForeignWordsSupp.add(foregin_word);
				notSortedListForNativWordsSupp.add(nativ_word);
				transArraySupp.add(trans);

			}while(cursor.moveToNext());	
		}
		cursor.close();
		myDataBase.close();
	}

}
