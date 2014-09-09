package com.johnhiott.sundayfunday;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

import android.location.Location;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.johnhiott.sundayfunday.fragments.CustomListFragment;
import com.johnhiott.sundayfunday.fragments.CustomMapFragment;
import com.johnhiott.sundayfunday.models.Place;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends Activity implements
      GooglePlayServicesClient.ConnectionCallbacks,
      GooglePlayServicesClient.OnConnectionFailedListener {


  public final static int LOCATIONS_ALL = 0;
  public final static int LOCATION_STORE = 1;
  public final static int LOCATION_BAR = 2 ;

  private LocationClient mLocationClient;
  private String[] navTitles;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private CharSequence mDrawerTitle;
  private CharSequence mTitle;
  private CharSequence[] mTabTitles;
  private static final int PAGE_COUNT = 2;
  private Fragment currentFragment;
  private Location mLocation;
  private int mLocationType;
  private DrawerLayout mDrawerLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //TODO: lot's of error handling for locations and play services missing

    mLocationType = MainActivity.LOCATIONS_ALL; //TODO:move to config
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    mTitle = getTitle();
    mDrawerTitle = "Select Type of Location";

    navTitles = getResources().getStringArray(R.array.nav_options);

    mDrawerList = (ListView) findViewById(R.id.left_drawer);

    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mLocationType = i;
        mDrawerLayout.closeDrawer(mDrawerList);
        makeNetworkCall();

      }
    });

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

    setupTabs();

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

    //TODO: handling no location
    Log.v("HIOTTJOHN", "Location Services Connected");
    mLocation = mLocationClient.getLastLocation();

    makeNetworkCall();

  }

  @Override
  public void onDisconnected() {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  public void setupTabs(){

    final ActionBar actionBar = getActionBar();
    mTabTitles = getResources().getStringArray(R.array.tab_titles);

    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
      @Override
      public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //TODO: store these keys in resources or as const
        switch (tab.getPosition()){
          case 0:
             changeFragment("map");
             break;
          case 1:
            changeFragment("list");
            break;
        }
      }

      @Override
      public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

      }

      @Override
      public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

      }

    };

    for (int x=0; x<PAGE_COUNT; x++){
      actionBar.addTab(actionBar.newTab()
            .setText(mTabTitles[x])
            .setTabListener(tabListener)
      );
    }
  }

  /*
    Use the fragmentmanager to handle switching between tabs.
   */
  public void changeFragment(String tag){

    Fragment tmpFragment;
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

    //look to see if we have already added the fragment
    tmpFragment = getFragmentManager().findFragmentByTag(tag);

    //we currently have a fragment, hide it
    if (currentFragment != null){
      fragmentTransaction.detach(currentFragment);
    }

    //did not find the fragment
    if (tmpFragment == null){
      if (tag.equals("map")){
        currentFragment = CustomMapFragment.newInstance();
      } else if (tag.equals("list")){
        currentFragment = CustomListFragment.newInstance();
      }else {
        //TODO
      }
      fragmentTransaction.add(R.id.content_frame, currentFragment, tag); //add fragment for 1st time
    } else {
      //we found the fragment
      currentFragment = tmpFragment;
      fragmentTransaction.attach(currentFragment);  //show the fragment we found
    }
    fragmentTransaction.commit();
  }


  private String buildUrl(){

    double currentLat = mLocation.getLatitude();
    double currentLon = mLocation.getLongitude();

    //temp stuff
    currentLat = 34.9883880;
    currentLon = -81.2377340;

    String baseUrl = "http://johnhiott.com/sundayfunday/locations.php?lat=" + currentLat + "&lon=" + currentLon;

    switch (mLocationType){
      case LOCATIONS_ALL:
        return baseUrl;
      case LOCATION_BAR:
        return baseUrl + "&type=bar";
      case LOCATION_STORE:
        return baseUrl + "&type=store";
    }
    return baseUrl;
  }

  private void makeNetworkCall(){
    OkHttpClient okHttpClient = new OkHttpClient();

    String url = buildUrl();

    Request request = new Request.Builder()
          .url(url)
          .build();

    Call call = okHttpClient.newCall(request);

    call.enqueue(new Callback() {
      @Override
      public void onFailure(Request request, IOException e) {
        Log.e("Network Call", "Network call failed");
      }

      /*
        Once network request is complete, add markers to map
       */
      @Override
      public void onResponse(Response response) throws IOException {
        Log.v ("HIOTTJOHN", "Network response");
        Gson gson = new Gson();
        MainApplication application = (MainApplication)getApplication();
        application.setPlaces( gson.fromJson(response.body().string(), Place[].class) );
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            CustomMapFragment customMapFragment = (CustomMapFragment)getFragmentManager().findFragmentByTag("map");
            if (customMapFragment != null)  //make sure fragment is there
              customMapFragment.setupMap();

            CustomListFragment listFragment = (CustomListFragment)getFragmentManager().findFragmentByTag("list");
            if (listFragment != null)   //make sure the fragment is there
              listFragment.setAdapter();
          }
        });
      }
    });
  }

}