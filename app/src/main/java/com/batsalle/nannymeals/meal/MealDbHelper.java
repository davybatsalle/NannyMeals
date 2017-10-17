package com.batsalle.nannymeals.meal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davyb on 16/10/2017.
 */

public class MealDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + MealContract.MealTable.TABLE_NAME + " (" +
                    MealContract.MealTable.COLUMN_DATE + " TEXT PRIMARY KEY," +
                    MealContract.MealTable.COLUMN_PLAT+ " TEXT," +
                    MealContract.MealTable.COLUMN_FROMAGE+ " TEXT," +
                    MealContract.MealTable.COLUMN_DESSERT+ " TEXT," +
                    MealContract.MealTable.COLUMN_GOUTER+ " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MealContract.MealTable.TABLE_NAME;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "nannyMeals.db";

    private SQLiteDatabase writableDb = null;
    private SQLiteDatabase readableDb = null;

    MealDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    void addItem(MealContent.MealItem item) {
        this.initWritable();
        ContentValues values = new ContentValues();
        values.put(MealContract.MealTable.COLUMN_DATE, item.date);
        values.put(MealContract.MealTable.COLUMN_PLAT, item.plat);
        values.put(MealContract.MealTable.COLUMN_FROMAGE, item.fromage);
        values.put(MealContract.MealTable.COLUMN_DESSERT, item.dessert);
        values.put(MealContract.MealTable.COLUMN_GOUTER, item.gouter);

        this.writableDb.insert(MealContract.MealTable.TABLE_NAME, null, values);
    }

    void removeItem(String date) {
        this.initWritable();
        String where = MealContract.MealTable.COLUMN_DATE + " like ?";
        String[] whereArgs = { date };
        this.writableDb.delete(MealContract.MealTable.TABLE_NAME, where, whereArgs);
    }

    void updateItem(MealContent.MealItem item) {
        this.initWritable();
        //TODO : add update sql
        ContentValues values = new ContentValues();
        values.put(MealContract.MealTable.COLUMN_DATE, item.date);
        values.put(MealContract.MealTable.COLUMN_PLAT, item.plat);
        values.put(MealContract.MealTable.COLUMN_FROMAGE, item.fromage);
        values.put(MealContract.MealTable.COLUMN_DESSERT, item.dessert);
        values.put(MealContract.MealTable.COLUMN_GOUTER, item.gouter);

        String selection = MealContract.MealTable.COLUMN_DATE + " like ?";
        String[] selectionArgs = { item.date };

        this.writableDb.update(
                MealContract.MealTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

    }

    List<MealContent.MealItem> retrieveItems() {
        this.initReadable();
        String[] projection = {MealContract.MealTable.COLUMN_DATE,
                MealContract.MealTable.COLUMN_PLAT,
                MealContract.MealTable.COLUMN_FROMAGE,
                MealContract.MealTable.COLUMN_DESSERT,
                MealContract.MealTable.COLUMN_GOUTER};
        String sortOrder = MealContract.MealTable.COLUMN_DATE + " DESC";

        Cursor cursor = this.readableDb.query(
                MealContract.MealTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        List<MealContent.MealItem> items = new ArrayList<MealContent.MealItem>();
        Integer i = 0;
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(MealContract.MealTable.COLUMN_DATE));
            String plat = cursor.getString(cursor.getColumnIndexOrThrow(MealContract.MealTable.COLUMN_PLAT));
            String fromage = cursor.getString(cursor.getColumnIndexOrThrow(MealContract.MealTable.COLUMN_FROMAGE));
            String dessert = cursor.getString(cursor.getColumnIndexOrThrow(MealContract.MealTable.COLUMN_DESSERT));
            String gouter = cursor.getString(cursor.getColumnIndexOrThrow(MealContract.MealTable.COLUMN_GOUTER));
            MealContent.MealItem item = new MealContent.MealItem(date,plat,fromage,dessert,gouter);
            items.add(i, item);
        }

        cursor.close();

        return items;
    }

    public void closeDb() {
        if (this.writableDb != null) {
            this.writableDb.close();
        }

        if (this.readableDb != null) {
            this.readableDb.close();
        }
    }


    private void initWritable() {
        if (null == this.writableDb) {
            this.writableDb = this.getWritableDatabase();
        }
    }

    private void initReadable() {
        if (null == this.readableDb) {
            this.readableDb = this.getReadableDatabase();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




}
