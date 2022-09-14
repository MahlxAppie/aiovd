package com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbmodels;

import android.util.Log;

import com.google.android.gms.measurement.api.AppMeasurementSdk;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class FBUserData {
    public List<FBfriendStory> friends;

    public String id;
    public String name;
    public String profilePic;

    public FBUserData(String str, String str2, String str3, List<FBfriendStory> list) {
        this.name = str;
        this.id = str2;
        this.profilePic = str3;
        this.friends = list;
    }

    public static FBUserData parse(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getJSONObject("data").getJSONObject("me").getString(AppMeasurementSdk.ConditionalUserProperty.NAME);
            String string2 = jSONObject.getJSONObject("data").getJSONObject("me").getString("id");
            String string3 = jSONObject.getJSONObject("data").getJSONObject("me").getJSONObject("profile_picture").getString("uri");
            List<FBfriendStory> parseBulk = FBfriendStory.parseBulk(jSONObject.getJSONObject("data").getJSONObject("me").getJSONObject("unified_stories_buckets").getJSONArray("edges"));
            int i = 0;
            while (i < Objects.requireNonNull(parseBulk).size()) {
                String str2 = parseBulk.get(i).uid;
                if (string2.equals(str2)) {
                    Log.e("tag2", "user story found and removed");
                    parseBulk.remove(i);
                    i--;
                }
                i++;
            }
            return new FBUserData(string, string2, string3, parseBulk);
        } catch (Exception e) {
            Log.e("tag1", "error while parsing FBUserData " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
