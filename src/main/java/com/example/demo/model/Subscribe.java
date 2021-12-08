package com.example.demo.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "subscribe")
@Entity
public class Subscribe {
    @EmbeddedId
    private SubscribeId id;

    public SubscribeId getId() {
        return id;
    }

    public void setId(SubscribeId id) {
        this.id = id;
    }
}