package com.infusiblecoder.allinonevideodownloader.models.bulkdownloader;

import androidx.annotation.Keep;

@Keep
public class DimensionsInfo {
    int height;
    int width;

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
