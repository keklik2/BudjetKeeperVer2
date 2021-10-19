package com.demo.budjetkeeperver2.billsdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Bill.class}, version = 1, exportSchema = false)
public abstract class BillsDatabase extends RoomDatabase {
    private static BillsDatabase database;
    private static final String DB_NAME = "bills.db";

    public static synchronized BillsDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, BillsDatabase.class,
                    DB_NAME)
                    .build();
        }
        return database;
    }

    public abstract BillsDao billsDao();
}
