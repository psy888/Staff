package com.psy.staff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DbHeleper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String TABLE_STAFF = "staff";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FIRST_NAME = "firstname";
    private static final String COLUMN_LAST_NAME = "lastname";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_BIRTHDAY = "birthday";


    public DbHeleper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_STAFF + "(" +
                COLUMN_ID + " INTEGER  NOT NULL  PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIRST_NAME + " TEXT NOT NULL," +
                COLUMN_LAST_NAME + " TEXT NOT NULL," +
                COLUMN_GENDER + " INTEGER," +
                COLUMN_BIRTHDAY + "TEXT );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addHuman(Human h){
        if(h==null) return;
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FIRST_NAME, h.mFirstName);
        cv.put(COLUMN_LAST_NAME, h.mLastName);
        cv.put(COLUMN_GENDER, (h.mGender)?1:0);
        cv.put(COLUMN_BIRTHDAY, h.getBirthDateString());

        SQLiteDatabase db = this.getWritableDatabase();
        long id =  db.insert(TABLE_STAFF, null, cv);
        if(id>0){
            MainActivity.mkToast("Added!");
        }else{
            MainActivity.mkToast("Error");
        }
        db.close();
    }

    public void editHuman(Human h, ){
        String[] selectionArgs = new String{h.mFirstName, h.mLastName};
        Cursor c = this.getWritableDatabase().query(TABLE_STAFF,new String[]{COLUMN_ID},
                COLUMN_FIRST_NAME +" LIKE ? AND " + COLUMN_LAST_NAME + " LIKE ? ",
                selectionArgs,
                null,
                null,
                null);
        int id = c.getInt(c.getColumnIndex(COLUMN_ID));
    }
}
