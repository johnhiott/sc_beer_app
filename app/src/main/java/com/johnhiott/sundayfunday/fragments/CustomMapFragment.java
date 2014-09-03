package com.johnhiott.sundayfunday.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.johnhiott.sundayfunday.MainApplication;
import com.johnhiott.sundayfunday.models.Place;

public class CustomMapFragment extends MapFragment {

  private CameraPosition cameraPosition;
  private LatLngBounds.Builder builder;
  private Boolean zoomFixed = false;

  @Override
  public void onPause() {
    super.onPause();
    MainApplication application = (MainApplication) getActivity().getApplication();
    application.setCameraPosition(getMap().getCameraPosition());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    MainApplication application = (MainApplication)getActivity().getApplication();
    Place[] places = application.getPlaces();

    builder = new LatLngBounds.Builder();

    for (Place place : places ){
      LatLng latLng = new LatLng(place.getLat(), place.getLon());
      builder.include(latLng);
      getMap().addMarker(new MarkerOptions().position(latLng).title(place.getName()));
    }

    //wait until map is finished building to change camera
    getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
      @Override
      public void onCameraChange(CameraPosition cameraPosition) {
        if (zoomFixed == false) {
          LatLngBounds latLngBounds = builder.build();
          getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 1));
          zoomFixed = true;
        }
      }
    });
  }

  public static CustomMapFragment newInstance() {
    CustomMapFragment fragment = new CustomMapFragment();
    return fragment;
  }

}