package com.example.demo.model;

import javax.persistence.*;

@Table(name = "good")
@Entity
public class Good {
    @Id
    @Column(name = "good_id", nullable = false, length = 16)
    private String id;

    @Column(name = "good_name", nullable = false, length = 32)
    private String name;

    @Column(name = "seller_id", length = 16)
    private String sellerId;

    @Column(name = "part", length = 10)
    private String part;

    @Column(name = "inventory")
    private Integer inventory;

    @Column(name = "info", length = 200)
    private String info;

    @Column(name = "price")
    private Double price;

    @Column(name = "freight")
    private Double freight;

    @Lob
    @Column(name = "good_state")
    private String goodState;

    @Column(name="pict_url")
    private String url;

    @Column(name="ship_address")
    private String ship_address;

    @Column(name="is_rec",length = 2)
    private String isRec;

    public String getIsRec() {
        return isRec;
    }

    public void setIsRec(String isRec) {
        this.isRec = isRec;
    }

    public String getShip_address() {
        return ship_address;
    }

    public void setShip_address(String ship_address) {
        this.ship_address = ship_address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGoodState() {
        return goodState;
    }

    public void setGoodState(String goodState) {
        this.goodState = goodState;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

}