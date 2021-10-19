package com.demo.budjetkeeperver2.categorydb;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int type; // 0 - трата, 1 - пополнение денег (получение)

    public Category(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Ignore
    public Category(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
