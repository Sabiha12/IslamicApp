package com.sabiha.islamicapp.model;

public class PrayerTimeModel {

    private int id;
    private String prayerName;
    private String prayerTime;

    public PrayerTimeModel(int id, String prayerName, String prayerTime) {
        this.id = id;
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
    }

    public PrayerTimeModel(String prayerName, String prayerTime) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrayerName() {
        return prayerName;
    }

    public void setPrayerName(String prayerName) {
        this.prayerName = prayerName;
    }

    public String getPrayerTime() {
        return prayerTime;
    }

    public void setPrayerTime(String prayerTime) {
        this.prayerTime = prayerTime;
    }
}
