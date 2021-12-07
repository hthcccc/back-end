package com.example.demo.model;

import javax.persistence.*;

@Table(name = "refund")
@Entity
public class Refund {
    @Id
    @Column(name = "order_id", nullable = false, length = 16)
    private String id;

    @Column(name = "text", length = 100)
    private String text;

    @Lob
    @Column(name = "refund_state")
    private String refundState;

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}