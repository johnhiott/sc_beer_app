package com.johnhiott.sundayfunday;

import android.app.Application;

import com.parse.Parse;

public class MainApplication extends Application {

  final static private String APP_ID = "QizCTPYAvWLehwyZoqMNq64izrojCTBMX7COgEKR";
  final static private String CLIENT_KEY = "V6p7RBjliGPB7AZcBxatoyFLpylgLTTYb3o5sfcH";

  @Override
  public void onCreate(){
    super.onCreate();

    Parse.initialize(this, APP_ID, CLIENT_KEY);

  }
}
