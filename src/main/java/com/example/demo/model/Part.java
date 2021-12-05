package com.example.demo.model;

import javax.persistence.*;

@Table(name = "part")
@Entity
public class Part {

  @Id
  @Column(name="part", nullable = false, length = 16)
  private String part;

  public String getPart() {
    return part;
  }

  public void setPart(String part) {
    this.part = part;
  }

}
