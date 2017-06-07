package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class NewsListEntity implements Serializable {
    private String icon;
    private String title;
    private String simplify_content;
    private String url;
    private String type;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSimplify_content() {
        return simplify_content;
    }

    public void setSimplify_content(String simplify_content) {
        this.simplify_content = simplify_content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
