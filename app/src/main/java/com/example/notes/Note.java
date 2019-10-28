package com.example.notes;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String content;
    private Date creationDate;

    public Note(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
