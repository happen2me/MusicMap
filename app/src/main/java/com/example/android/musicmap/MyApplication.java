package com.example.android.musicmap;
import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.litepal.LitePal;

/**
 * Created by 申源春 on 2017/7/5.
 */

public class MyApplication extends Application{
    private static Context context;
    private static boolean googlePlayServicesAvailable;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        googlePlayServicesAvailable = (resultCode == ConnectionResult.SUCCESS);
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }

    public static boolean isGooglePlayServicesAvailable(){
        return googlePlayServicesAvailable;
    }
}
