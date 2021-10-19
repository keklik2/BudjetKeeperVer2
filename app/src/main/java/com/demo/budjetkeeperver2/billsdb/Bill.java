package com.demo.budjetkeeperver2.billsdb;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bills")
public class Bill {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String bankName;
    private double money;

    public Bill(int id, String title, String bankName, double money) {
        this.id = id;
        this.title = title;
        this.bankName = bankName;
        this.money = money;
    }

    @Ignore
    public Bill(String title, String bankName, double money) {
        this.title = title;
        this.bankName = bankName;
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBankName() {
        return bankName;
    }

    public double getMoney() {
        return money;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
