package com.example.android.musicmap;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 申源春 on 2017/7/10.
 */

public class ActivityCollector {

    public static List<Activity> sActivities = new ArrayList<>();

    public static void addActivity(Activity activity){
        sActivities.add(activity);
    }

    public static void removeActivity(Activity activity){
        sActivities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity : sActivities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
