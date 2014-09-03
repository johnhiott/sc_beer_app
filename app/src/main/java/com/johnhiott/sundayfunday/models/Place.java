package com.johnhiott.sundayfunday.models;

import java.io.Serializable;

public class Place implements Serializable {

  private String name;
  private String address;
  private double lat;
  private double lon;
  private String license;
  private double distance;

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

  public String getLicense() {
    return license;
  }

  public double getDistance(){
    return distance;
  }

}
