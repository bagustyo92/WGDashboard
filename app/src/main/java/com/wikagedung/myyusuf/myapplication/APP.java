package com.wikagedung.myyusuf.myapplication;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Bagus on 18/07/2017.
 */

public class APP extends Application {
    private final SharedPreferences settings = getSharedPreferences("prefs", 0);
    private final SharedPreferences.Editor editor = settings.edit();

    public SharedPreferences.Editor editSharePrefs() {
        return editor;
    }
}
