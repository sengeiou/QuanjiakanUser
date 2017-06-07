package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class BaseHttpResultEntity_List<T> implements Serializable {
    private List<T> rows;
    private int total;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
