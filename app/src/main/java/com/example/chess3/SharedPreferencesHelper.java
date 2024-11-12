package com.example.chess3;



import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void putData(String key, String value) {
        editor.putString(key, value).apply();
    }

    public String getData(String key) {
        return sharedPreferences.getString(key, "");
    }


}
