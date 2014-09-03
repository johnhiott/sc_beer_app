package com.johnhiott.sundayfunday;

import android.app.Application;

import com.google.android.gms.maps.model.CameraPosition;
import com.johnhiott.sundayfunday.models.Place;

public class MainApplication extends Application {

  private CameraPosition cameraPosition;
  private Place[] places;

  @Override
  public void onCreate(){
    super.onCreate();
  }

  public Place[] getPlaces() {
    return places;
  }

  public void setPlaces(Place[] places) {
    this.places = places;
  }

  public void setCameraPosition(CameraPosition cameraPosition){
    this.cameraPosition = cameraPosition;
  }

  public CameraPosition getCameraPosition(){
    return this.cameraPosition;
  }
}
