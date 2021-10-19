package com.demo.budjetkeeperver2.categorydb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class CategoriesDatabase extends RoomDatabase {
    private static CategoriesDatabase database;
    private static final String DB_NAME = "categories.db";

    public static synchronized CategoriesDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, CategoriesDatabase.class, DB_NAME)
                    .build();
        }
        return database;
    }

    public abstract CategoriesDao categoriesDao();
}
