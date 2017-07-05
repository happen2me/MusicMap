package com.example.android.musicmap;
import android.app.Application;
import android.content.Context;
/**
 * Created by 申源春 on 2017/7/5.
 */

public class MyApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
