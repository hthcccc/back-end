package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "recrecord")
@Entity
public class Recrecord {
    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "good_id", length = 45)
    private String goodId;

    @Column(name = "rec_id", length = 45)
    private String recId;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "state", length = 45)
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}