package com.example.notes.App;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Note implements Serializable {
    private String content;
    private String exactCreationDate;
    private String creationDate;

    public Note(String content) {
        this.content = content;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = DateFormat.getDateInstance();
        exactCreationDate = dateTimeFormat.format(calendar.getTime());
        creationDate = dateFormat.format(calendar.getTime());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getExactCreationDate() {
        return exactCreationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
