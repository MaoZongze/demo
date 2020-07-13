package com.mzz.demo;

import java.time.LocalDateTime;

public class SimpleDocument {

    private String title;

    private String doc;

    private LocalDateTime created;

    private LocalDateTime updated;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleDocument{");
        sb.append("title='").append(title).append('\'');
        sb.append(", doc='").append(doc).append('\'');
        sb.append(", created=").append(created);
        sb.append(", updated=").append(updated);
        sb.append('}');
        return sb.toString();
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public SimpleDocument(String title, String doc, LocalDateTime created, LocalDateTime updated) {
        this.title = title;
        this.doc = doc;
        this.created = created;
        this.updated = updated;
    }

    public SimpleDocument(String title, String desc) {
        this.title = title;
        this.doc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

}
