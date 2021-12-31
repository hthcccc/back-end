package com.example.demo.model;

import javax.persistence.*;
import java.time.Instant;

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

    @Column(name = "refund_time")
    private Instant refund_time;

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState;
    }

    public Instant getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(Instant refund_time) {
        this.refund_time = refund_time;
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