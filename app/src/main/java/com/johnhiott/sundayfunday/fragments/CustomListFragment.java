package com.johnhiott.sundayfunday.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.johnhiott.sundayfunday.MainApplication;
import com.johnhiott.sundayfunday.PlaceListAdapter;
import com.johnhiott.sundayfunday.R;
import com.johnhiott.sundayfunday.models.Place;

import java.util.Arrays;
import java.util.List;

public class CustomListFragment extends ListFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.list_fragment, parent, false);
    setAdapter();
    return view;
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  public static CustomListFragment newInstance() {
    CustomListFragment fragment = new CustomListFragment();
    return fragment;
  }

  public void setAdapter(){
    MainApplication application = (MainApplication)getActivity().getApplication();
    Place[] places = application.getPlaces();
    PlaceListAdapter placeListAdapter = new PlaceListAdapter(getActivity(), Arrays.asList(places));
    setListAdapter( placeListAdapter );
  }
}