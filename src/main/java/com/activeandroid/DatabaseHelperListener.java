package com.activeandroid;

import android.database.sqlite.SQLiteDatabase;

public interface DatabaseHelperListener {

    public void onOpen(SQLiteDatabase database);

    public void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
