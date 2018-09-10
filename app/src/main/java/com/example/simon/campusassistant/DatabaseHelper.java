package com.example.simon.campusassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Xinmeng Lyu.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "clock.db";
    public static final String TABLE_NAME = "clock_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TIME";
    public static final String COL_3 = "TITLE";


    public DatabaseHelper(Context context, String s, Object o, int i) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME LONG, TITLE TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(Long TIME, String TITLE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,TIME);
        contentValues.put(COL_3,TITLE);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        } else {
            return true;
        }
    }
/*
    public void update(int i,int _id,long time,String title,String location){
        SQLiteDatabase db=DatabaseHelper.this.getWritableDatabase();
        String where="_id = ?";
        String[] whereValues={Integer.toString(_id)};
        ContentValues cv=new ContentValues();
        if(time!=0) cv.put(COL_2,time);
        if(!title.equals("")) cv.put(COL_3, title);
   //     if(!location.equals("")) cv.put(COL_4,location);
        db.update(TABLE_NAME, cv, where, whereValues);
    }
    */
    /*
    public void deleteData(int i,int _id){
        SQLiteDatabase db=DatabaseHelper.this.getWritableDatabase();
        String where="_id = ?";
        String[] whereValues={Integer.toString(_id)};
        ContentValues cv=new ContentValues();
        cv.put("TIME",0);
        cv.put("location","");
        cv.put("teacher","");
        cv.put("time1","");
        cv.put("time2","");
        db.update(TB_NAME[i], cv, where, whereValues);
    }
    */
}
