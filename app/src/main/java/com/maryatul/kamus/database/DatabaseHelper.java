package com.maryatul.kamus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper{

    public static String DATABASE_NAME = "dbs_kamus";

    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_MELAYU = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_MELAYU,
            DatabaseContract.KamusColumns._ID,
            DatabaseContract.KamusColumns.KATA,
            DatabaseContract.KamusColumns.DESKRIPSI
    );

    private static final String CREATE_TABLE_INDONESIA = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_INDONESIA,
            DatabaseContract.KamusColumns._ID,
            DatabaseContract.KamusColumns.KATA,
            DatabaseContract.KamusColumns.DESKRIPSI
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MELAYU);
        db.execSQL(CREATE_TABLE_INDONESIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_MELAYU);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_INDONESIA);
        onCreate(db);
    }
}
