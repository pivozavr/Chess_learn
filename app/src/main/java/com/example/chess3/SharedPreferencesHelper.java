package com.example.chess3;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MyPreference", 0);
        this.editor = sharedPreferences.edit();
    }

    public void putData(String key, String value) {
        editor.putString(key, value).apply();
    }

    public String getData(String key) {
        return sharedPreferences.getString(key, "");
    }


}
