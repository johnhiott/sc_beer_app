package com.johnhiott.sundayfunday;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.johnhiott.sundayfunday.models.Place;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements
      GooglePlayServicesClient.ConnectionCallbacks,
      GooglePlayServicesClient.OnConnectionFailedListener {

  private Location mLocation;
  private LocationClient mLocationClient;
  private GoogleMap mMap;
  private OkHttpClient mOkHttpClient;
  private Context mContext;
  private String mResponse;
  private Place[] places;
  private Gson gson;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mContext = getApplicationContext();

    mLocationClient = new LocationClient(this,this,this);
    mLocationClient.connect();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConnected(Bundle bundle) {

    Location location = mLocationClient.getLastLocation();

    double currentLat = location.getLatitude();
    double currentLon = location.getLongitude();

    mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
          .getMap();

    gson = new Gson();
    ApiRequest apiRequest = new ApiRequest();

    OkHttpClient okHttpClient = new OkHttpClient();

    String url = "http://johnhiott.com/sundayfunday/locations.php?lat=" + currentLat + "&lon=" + currentLon;

    Request request = new Request.Builder()
          .url(url)
          .build();

    Call call = okHttpClient.newCall(request);

    call.enqueue(new Callback() {
      @Override
      public void onFailure(Request request, IOException e) {

      }

      @Override
      public void onResponse(Response response) throws IOException {
        mResponse = response.body().string();
        places = gson.fromJson(mResponse, Place[].class);

        runOnUiThread(new Runnable() {
          public void run() {
            int count = places.length;
            for (int x=0; x<count; x++){

              LatLng latLng = new LatLng(places[x].getLat(), places[x].getLon());
              mMap.addMarker(new MarkerOptions().position(latLng).title(places[x].getName()));

            }
          }
        });
      }
    });
  }

  @Override
  public void onDisconnected() {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }
}