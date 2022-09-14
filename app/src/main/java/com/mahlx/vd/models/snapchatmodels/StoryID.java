package com.infusiblecoder.allinonevideodownloader.models.snapchatmodels;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StoryID implements Serializable {
    @SerializedName("value")
    private String value;

    @SerializedName("value")
    public String getValue() {
        return value;
    }

    @SerializedName("value")
    public void setValue(String value) {
        this.value = value;
    }
}
