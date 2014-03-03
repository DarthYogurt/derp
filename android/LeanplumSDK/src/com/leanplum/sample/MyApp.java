package com.leanplum.sample;

import com.leanplum.Leanplum;
import com.leanplum.LeanplumApplication;

public class MyApp extends LeanplumApplication {
  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Leanplum.setAppIdForDevelopmentMode(
          [INSERT APP ID HERE],
          [INSERT DEVELOPMENT KEY HERE]);
    } else {
      Leanplum.setAppIdForProductionMode(
          [INSERT APP ID HERE],
          [INSERT PRODUCTION KEY HERE]);
    }
  }
}
