package com.wolf.jumphelp;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Roye on 2018/1/10.
 */

public class RobotService extends AccessibilityService {

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d("www","onCreate ");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        android.util.Log.d("www","onAccessibilityEvent "+event.toString());
    }

    @Override
    public void onInterrupt() {

    }
}
