package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "chatgroup")
@Entity
public class Chatgroup {
    @Id
    @Column(name = "group_id", nullable = false, length = 32)
    private String id;

    @Column(name = "user1", length = 16)
    private String user1;

    @Column(name = "user2", length = 2)
    private String user2;

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}