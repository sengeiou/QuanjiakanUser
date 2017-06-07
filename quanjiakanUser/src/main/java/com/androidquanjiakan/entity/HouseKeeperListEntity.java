package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class HouseKeeperListEntity implements Serializable{
    private List<HouseKeeperEntity> rows;

    public List<HouseKeeperEntity> getRows() {
        return rows;
    }

    public void setRows(List<HouseKeeperEntity> rows) {
        this.rows = rows;
    }
}
