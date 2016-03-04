package com.cs437.esauceda.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by esauceda on 3/3/16.
 */

public class AnimalDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Animal.AnimalEntry.TABLE_NAME + " (" +
            Animal.AnimalEntry._ID + " INTEGER PRIMARY KEY," +
            Animal.AnimalEntry.ANIMAL_ENTRY_ID + " TEXT, " +
            Animal.AnimalEntry.ANIMAL_NAME + " TEXT, " +
            Animal.AnimalEntry.ANIMAL_SCI_NAME + " TEXT, " +
            Animal.AnimalEntry.ANIMAL_ORIGIN + " TEXT, " +
            Animal.AnimalEntry.ANIMAL_DESC + " TEXT, " +
            Animal.AnimalEntry.ANIMAL_IMG + " TEXT )";

    public AnimalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE if EXISTS animals");
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS animals");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<String> getData()
    {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from animals", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Animal.AnimalEntry.ANIMAL_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean insertAnimal (String name, String sci_name, String origin, String desc,String img)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("animal_name", name);
        contentValues.put("scientific_name", sci_name);
        contentValues.put("origin", origin);
        contentValues.put("description", desc);
        contentValues.put("image_url", img);
        db.insert("animals", null, contentValues);
        return true;
    }
    public boolean clearDB(SQLiteDatabase db){
        db.execSQL("DELETE FROM animals");
        return true;
    }

    public HashMap<String, String> getAnimal(String name){
        HashMap<String, String> animal = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from animals where animal_name = '"+name+"'", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            animal.put("animal_name", res.getString(res.getColumnIndex(Animal.AnimalEntry.ANIMAL_NAME)));
            animal.put("scientific_name", res.getString(res.getColumnIndex(Animal.AnimalEntry.ANIMAL_SCI_NAME)));
            animal.put("origin", res.getString(res.getColumnIndex(Animal.AnimalEntry.ANIMAL_ORIGIN)));
            animal.put("description", res.getString(res.getColumnIndex(Animal.AnimalEntry.ANIMAL_DESC)));
            animal.put("image_url", res.getString(res.getColumnIndex(Animal.AnimalEntry.ANIMAL_IMG)));
            res.moveToNext();
        }
        return animal;
    }
}
