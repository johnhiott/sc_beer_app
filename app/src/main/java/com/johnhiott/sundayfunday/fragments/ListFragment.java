package com.johnhiott.sundayfunday.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.johnhiott.sundayfunday.R;

public class ListFragment extends Fragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.list_fragment, parent, false);
    return view;
  }

  public static ListFragment newInstance() {
    ListFragment fragment = new ListFragment();
    return fragment;
  }
}