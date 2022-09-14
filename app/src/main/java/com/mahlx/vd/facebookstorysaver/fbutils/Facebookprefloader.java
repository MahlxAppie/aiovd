package com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbutils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbmodels.ModelFacebookpref;

public class Facebookprefloader {

    public static String DataFileName = "fb_prefvals";
    public static String PREFERENCE_itemFB = "fb_prefvals_item";

    public Context context;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    public Facebookprefloader(Context context) {
        this.context = context;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        sharedPreference = context.getSharedPreferences(DataFileName, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }


    public ModelFacebookpref LoadPrefString() {
        if (sharedPreference == null) {
            sharedPreference = context.getSharedPreferences(DataFileName, Context.MODE_PRIVATE);
            editor = sharedPreference.edit();
        }
        try {
            Gson gson = new Gson();
            String storedHashMapString = sharedPreference.getString(PREFERENCE_itemFB, "oopsDintWork");


            return gson.fromJson(storedHashMapString, ModelFacebookpref.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ModelFacebookpref("", "", "false");
        }
    }

    public void SavePref(String str2, String str3) {
        if (editor == null) {
            sharedPreference = context.getSharedPreferences(DataFileName, Context.MODE_PRIVATE);
            editor = sharedPreference.edit();
        }

        ModelFacebookpref modelFacebookpref = new ModelFacebookpref(str2, str2, str3);


        Gson gson = new Gson();
        String hashMapString = gson.toJson(modelFacebookpref);

        editor = sharedPreference.edit();
        editor.putString(PREFERENCE_itemFB, hashMapString);
        editor.apply();


    }

    public void MakePrefEmpty() {
        if (editor == null) {
            sharedPreference = context.getSharedPreferences(DataFileName, Context.MODE_PRIVATE);
            editor = sharedPreference.edit();
        }

        ModelFacebookpref modelFacebookpref = new ModelFacebookpref("", "", "false");


        Gson gson = new Gson();
        String hashMapString = gson.toJson(modelFacebookpref);

        editor = sharedPreference.edit();
        editor.putString(PREFERENCE_itemFB, hashMapString);
        editor.apply();
    }


}
