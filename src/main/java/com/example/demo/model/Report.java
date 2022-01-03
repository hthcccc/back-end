package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "report")
@Entity
public class Report {
    @Id
    @Column(name = "report_id", nullable = false, length = 10)
    private String id;

    @Column(name = "user_id", length = 16)
    private String userId;

    @Column(name = "good_id", length = 16)
    private String goodId;

    @Column(name = "text", length = 500)
    private String text;

    @Column(name = "date")
    private Instant date;

    @Column(name = "report_state", length = 20)
    private String reportState;

    public String getReportState() {
        return reportState;
    }

    public void setReportState(String reportState) {
        this.reportState = reportState;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}