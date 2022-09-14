package com.infusiblecoder.allinonevideodownloader.models.bulkdownloader;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class EdgeOwnerToTimelineMedia {
    int count;
    PageInfo page_info;
    List<EdgeInfo> edges;

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PageInfo getPage_info() {
        return this.page_info;
    }

    public void setPage_info(PageInfo page_info) {
        this.page_info = page_info;
    }

    public List<EdgeInfo> getEdges() {
        return this.edges;
    }

    public void setEdges(List<EdgeInfo> edges) {
        this.edges = edges;
    }
}
