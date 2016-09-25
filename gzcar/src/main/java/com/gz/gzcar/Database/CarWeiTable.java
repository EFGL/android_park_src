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

    @Column(name = "info")
    private String info;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "CarWeiTable{" +
                "id=" + id +
                ", info='" + info + '\'' +
                '}';
    }
}
