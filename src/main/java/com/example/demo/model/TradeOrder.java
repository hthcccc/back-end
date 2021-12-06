package com.example.demo.model;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "trade_order")
@Entity
public class TradeOrder {
    @Id
    @Column(name = "order_id", nullable = false, length = 16)
    private String id;

    @Column(name = "buyer_id", length = 16)
    private String buyerId;

    @Column(name = "good_id", length = 16)
    private String goodId;

    @Column(name = "buyer_address", length = 32)
    private String buyerAddress;

    @Column(name = "seller_address", length = 32)
    private String sellerAddress;

    @Column(name = "num", nullable = false)
    private Integer num;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "start_date")
    private Instant startDate;

    @Lob
    @Column(name = "order_state", nullable = false)
    private String orderState;

    @Lob
    @Column(name = "is_refunding")
    private String isRefunding;

    public String getIsRefunding() {
        return isRefunding;
    }

    public void setIsRefunding(String isRefunding) {
        this.isRefunding = isRefunding;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}