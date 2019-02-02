package com.zero.dee.steps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";
    public static final String TABLE_DATA = "session";
    private static final String COLUMN_SESSIONSTEP = "sessionSteps";
    private static final String COLUMN_TOTALSTEPS= "Allsteps";
    boolean isFresh = false;
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!checkForTableExists(db, TABLE_DATA)){
            String query = "CREATE TABLE " + TABLE_DATA + " (" +
                    COLUMN_SESSIONSTEP + " STRING," +
                    COLUMN_TOTALSTEPS +  " STRING" + ");";
            isFresh = true;

            db.execSQL(query);
            Data data = new Data("0", "New Phone? Press Start");
            ContentValues valuesSess = new ContentValues();
            ContentValues valuesAll = new ContentValues();
            valuesSess.put(COLUMN_SESSIONSTEP, data.getSession_steps());
            valuesSess.put(COLUMN_TOTALSTEPS, data.get_Allsteps());
            db.insert(TABLE_DATA, null, valuesSess);
            db.insert(TABLE_DATA, null, valuesAll);

        }

    }

    //add new row
    public String updateData(Data data){
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        values.put(COLUMN_SESSIONSTEP, data.getSession_steps());
        values2.put(COLUMN_TOTALSTEPS, data.get_Allsteps());
        SQLiteDatabase db = getWritableDatabase();

        String checker ="";
        if (checkForTableExists(db, TABLE_DATA)){
//            if (isFresh){
//            db.insert(TABLE_DATA, null, values);
//            isFresh = false;
//            }else {
            db.update(TABLE_DATA, values, null, null);
            db.update(TABLE_DATA, values2, null, null);
//            }
            db.close();
            checker = "All Good";
            return checker;
        }else {

            checker = " DB doesnt exist.....";
            return checker;

        }

    }

    public String endSession(){
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSIONSTEP, "0");
        SQLiteDatabase db = getWritableDatabase();

        String checker ="";
        if (checkForTableExists(db, TABLE_DATA)){

            db.update(TABLE_DATA, values, null, null);
            db.close();
            checker = "Session Wiped 0/";
            return checker;
        }else {

            checker = " DB doesnt exist.....";
            return checker;

        }

    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);
        return cursor;
    }

    private boolean checkForTableExists(SQLiteDatabase db, String table){
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
    }

    public void updateTotal(float newSteps){
        SQLiteDatabase db = getWritableDatabase();

        float current = 0;
        db = this.getReadableDatabase();
        String selectQuery = "SELECT " + COLUMN_TOTALSTEPS + " FROM " + TABLE_DATA ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            current=  Float.parseFloat(cursor.getString(1));
        }
        /// Now we use condition --> if condition is positive it mean add ... if condition is negative it means
        ////subtract
        if(newSteps != 0){
            current += newSteps;
        }else {

        }
        cursor.close();
        db.close();

        String transferStr = String.valueOf(current);

        //Your Update to SQLite
        db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOTALSTEPS, transferStr);
        db.update(TABLE_DATA , values, null, null);
        db.close();

    }

    public void deleteData(String dataName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_DATA + " WHERE " + COLUMN_SESSIONSTEP + "=\"" + dataName + "\";" );

    }

    public String printDB(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DATA + " WHERE 1";

        //Cursor points to a locationm ion tour resilts
        Cursor c = db.rawQuery(query, null );
        //move to first row in result
        c.moveToFirst();

        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex("sessionSteps"))!=null){
                dbString += c.getString(c.getColumnIndex("sessionSteps"));
                dbString += "\n";
            }
        }

        db.close();

        return dbString;
    }








    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }
}
