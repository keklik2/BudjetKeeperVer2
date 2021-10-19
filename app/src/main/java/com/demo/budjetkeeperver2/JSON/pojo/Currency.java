package com.demo.budjetkeeperver2.JSON.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Currency {
    @Expose
    private boolean success;
    @SerializedName("terms")
    @Expose
    private String terms;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("timestamp")
    @Expose
    private int timestamp;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("quotes")
    @Expose
    private Quotes quotes;

    public boolean isSuccess() {
        return success;
    }

    public Quotes getQuotes() {
        return quotes;
    }
}
