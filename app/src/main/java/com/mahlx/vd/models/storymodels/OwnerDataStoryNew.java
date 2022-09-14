package com.infusiblecoder.allinonevideodownloader.models.storymodels;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class OwnerDataStoryNew implements Serializable {


    @SerializedName("pk")
    private String pk;
    @SerializedName("profile_pic_url")
    private String profile_pic_url;

    public OwnerDataStoryNew() {
    }

    public String getUsername() {
        return pk;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public void setUsername(String pk) {
        this.pk = pk;
    }
}
