package com.example.demo.controller.resultBody;

import java.time.Instant;

public class commentListBody {
    private String comment_id;
    private String name;
    private String text;

    public commentListBody() {};

    public commentListBody(String comment_id, String name, String text, Instant date) {
        this.comment_id = comment_id;
        this.name = name;
        this.text = text;
        this.date = date;
    }

    private Instant date;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }


}
