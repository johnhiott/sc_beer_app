package com.johnhiott.sundayfunday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.johnhiott.sundayfunday.models.Place;

import java.text.DecimalFormat;
import java.util.List;

public class PlaceListAdapter extends ArrayAdapter<Place> {

  private final Context context;
  private final List<Place> values;

  public PlaceListAdapter(Context context, List<Place> values) {
    super(context, R.layout.place_list_item, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View rowView = inflater.inflate(R.layout.place_list_item, parent, false);

    TextView textView;

    textView = (TextView)rowView.findViewById(R.id.name);
    textView.setText(values.get(position).getName());

    textView = (TextView)rowView.findViewById(R.id.distance);
    textView.setText(new DecimalFormat("##.##").format(values.get(position).getDistance()) + "mi");

    textView = (TextView)rowView.findViewById(R.id.type);
    if (values.get(position).getLicense().equals(Place.RESTAURANT_BAR_TYPE)){
      textView.setText("Restaurant/Bar");
    } else{
      textView.setText("Store");
    }

    return rowView;
  }
}
