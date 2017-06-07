package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class BaseHttpResultEntity<T> implements Serializable {
    private T rows;
    private int total;

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
