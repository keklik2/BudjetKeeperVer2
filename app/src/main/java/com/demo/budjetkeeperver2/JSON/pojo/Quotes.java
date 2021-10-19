package com.demo.budjetkeeperver2.JSON.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quotes {
    @SerializedName("USDEUR")
    @Expose
    private double usdeur;
    @SerializedName("USDRUB")
    @Expose
    private double usdrub;

    public double getUsdeur() {
        return usdeur;
    }

    public double getUsdrub() {
        return usdrub;
    }

    public double getRubusd() {
        return 1 / usdrub;
    }

    public void setUsdrub(double usdrub) {
        this.usdrub = usdrub;
    }
}
