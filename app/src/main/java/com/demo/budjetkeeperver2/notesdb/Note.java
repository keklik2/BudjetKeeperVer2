package com.demo.budjetkeeperver2.notesdb;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String category;
    /** поменять это поле на "categoryId", переделать логику, чтобы он сохранял ID категории */
    private int billId;
    private double price;
    private int type; // 0 - трата, 1 - прибавка денег
    private long date;

    public Note(int id, String title, String category, int billId, double price, int type, long date) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.billId = billId;
        this.price = price;
        this.type = type;
        this.date = date;
    }

    @Ignore
    public Note(String title, String category, int billId, double price, int type, long date) {
        this.title = title;
        this.category = category;
        this.billId = billId;
        this.price = price;
        this.type = type;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getBillId() {
        return billId;
    }

    public double getPrice() {
        return price;
    }

    public long getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
