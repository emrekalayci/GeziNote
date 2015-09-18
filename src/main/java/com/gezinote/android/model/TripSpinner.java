package com.gezinote.android.model;

/**
 * Created by Emre on 15.5.2015.
 */
public class TripSpinner {

    private int id;
    private String title;

    public TripSpinner() {
        super();
    }

    public TripSpinner (int id , String title) {
        this.id = id;
        this.title = title;
    }

    public int getId () {
        return id;
    }

    public String getTitle () {
        return title;
    }

    @Override
    public String toString () {
        return title;
    }

}
