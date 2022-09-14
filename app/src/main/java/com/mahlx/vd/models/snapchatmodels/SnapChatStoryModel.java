package com.infusiblecoder.allinonevideodownloader.models.snapchatmodels;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SnapChatStoryModel implements Serializable {
    @SerializedName("props")
    private Props props;

    @SerializedName("props")
    public Props getProps() {
        return props;
    }

    @SerializedName("props")
    public void setProps(Props value) {
        this.props = value;
    }
}
