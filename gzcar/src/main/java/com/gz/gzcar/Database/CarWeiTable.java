package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Endeavor on 2016/9/8.
 * <p/>
 * 设置 - 车位管理
 */
@Table(name = "vehicle_seat")
public class CarWeiTable {


    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "isUse")
    private boolean isUse = false;

    @Column(name = "info")
    private String info;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
