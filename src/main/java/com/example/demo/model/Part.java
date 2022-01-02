package com.example.demo.model;

import javax.persistence.*;

@Table(name = "part")
@Entity
public class Part {

  @Id
  @Column(name="part", nullable = false, length = 16)
  private String part;

  @Column(name="icon",length = 50)
  private String icon;

  @Column(name="info",length = 50)
  private String info;

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getPart() {
    return part;
  }

  public void setPart(String part) {
    this.part = part;
  }

}
