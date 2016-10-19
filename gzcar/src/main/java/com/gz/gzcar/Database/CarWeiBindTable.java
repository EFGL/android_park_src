package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Endeavor on 2016/10/17 0017.
 * <p>
 * 车位绑定表
 */

@Table(name = "car_bind")
public class CarWeiBindTable {
    @Column(name = "id", isId = true)
    private int id;
    //组键
    @Column(name = "code")
    private String code;
    //车号
    @Column(name = "car_no")
    private String car_no;
    //车位主键
    @Column(name = "stall_code")
    private String stall_code;
    //有效开始日期
    @Column(name = "begin_date")
    private Date begin_date;
    //有效截止日期
    @Column(name = "end_date")
    private Date end_date;
    //创建时间
    @Column(name = "created_at")
    private Date created_at;
    //更新时间
    @Column(name = "updated_at")
    private Date updated_at;
    //状态Y:N
    @Column(name = "status")
    private String status;

    @Override
    public String toString() {
        return "CarWeiBindTable{" +
                "code=" + id +
                ", car_no=" + car_no +
                ", stall_code=" + stall_code +
                ", begin_date='" + begin_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getStall_code() {
        return stall_code;
    }

    public void setStall_code(String stall_code) {
        this.stall_code = stall_code;
    }

    public Date getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(Date begin_date) {
        this.begin_date = begin_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

