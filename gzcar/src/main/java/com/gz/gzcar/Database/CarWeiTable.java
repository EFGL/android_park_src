package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Endeavor on 2016/9/8.
 *
 * 车位表
 */
@Table(name = "vehicle_seat")
public class CarWeiTable {
    @Column(name = "id", isId = true)
    private int id;
    //主键
    @Column(name = "stall_code")
    private String stall_code;
    //区域表主键
    @Column(name = "area_code")
    private String area_code;
    //车位编号，即显示名称
    @Column(name = "print_code")
    private String print_code;
    //创建时间
    @Column(name = "created_at")
    private Date created_at;
    //更新时间
    @Column(name = "updated_at")
    private Date updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStall_code() {
        return stall_code;
    }

    public void setStall_code(String stall_code) {
        this.stall_code = stall_code;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getPrint_code() {
        return print_code;
    }

    public void setPrint_code(String print_code) {
        this.print_code = print_code;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
