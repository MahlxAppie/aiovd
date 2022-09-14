package com.infusiblecoder.allinonevideodownloader.models.snapchatmodels;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Props implements Serializable {
    @SerializedName("pageProps")
    private PageProps pageProps;

    @SerializedName("pageProps")
    public PageProps getPageProps() {
        return pageProps;
    }

    @SerializedName("pageProps")
    public void setPageProps(PageProps value) {
        this.pageProps = value;
    }
}
