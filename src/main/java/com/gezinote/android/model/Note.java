package com.gezinote.android.model;

/**
 * Created by Emre on 18.4.2015.
 */
public class Note {
    private int id;
    private int tripId;
    private String content;
    private long time;
    private long createdAt;

    public Note() {
        super();
    }

    public Note(int id, int tripId, String content, long time, long createdAt) {
        super();
        this.id = id;
        this.tripId = tripId;
        this.content = content;
        this.time = time;
        this.createdAt = createdAt;
    }

    @Override

    public String toString() {
        return content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

