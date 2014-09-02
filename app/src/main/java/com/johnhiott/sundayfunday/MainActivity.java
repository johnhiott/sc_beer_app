package com.johnhiott.sundayfunday;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
  private String[] navTitles;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private CharSequence mDrawerTitle;
  private CharSequence mTitle;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //TODO: lot's of error handling for locations and play services missing

    mTitle = getTitle();
    mDrawerTitle = "Select Type of Location";

    mContext = getApplicationContext();

    navTitles = getResources().getStringArray(R.array.nav_options);

    mDrawerList = (ListView) findViewById(R.id.left_drawer);

    // Set the adapter for the list view
    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, navTitles));

    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    mDrawerToggle = new ActionBarDrawerToggle(
          this,                  /* host Activity */
          mDrawerLayout,         /* DrawerLayout object */
          R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
          R.string.drawer_open,  /* "open drawer" description */
          R.string.drawer_close  /* "close drawer" description */
    ) {

      /** Called when a drawer has settled in a completely closed state. */
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        getActionBar().setTitle(mTitle);
      }

      /** Called when a drawer has settled in a completely open state. */
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        getActionBar().setTitle(mDrawerTitle);
      }
    };

    // Set the drawer toggle as the DrawerListener
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);


    // Set the drawer toggle as the DrawerListener
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    mLocationClient = new LocationClient(this,this,this);
    mLocationClient.connect();

  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Pass the event to ActionBarDrawerToggle, if it returns
    // true, then it has handled the app icon touch event
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    // Handle your other action bar items...

    return super.onOptionsItemSelected(item);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
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