package com.gezinote.android.model;

/**
 * Created by Emre on 12.4.2015.
 */
public class Trip {
    private int id;
    private String title;
    private long startDate;
    private long endDate;
    private int colorId;

    public Trip() {
        super();
    }

    public Trip(int id, String title, long startDate, long endDate, int colorId) {
        super();
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.colorId = colorId;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String tripTitle) {
        this.title = tripTitle;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
}
