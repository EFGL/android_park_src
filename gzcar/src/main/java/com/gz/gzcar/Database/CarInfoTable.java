package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * 车辆信息信息表
 *
 * Created by Endeavor on 2016/8/18.
 */

@Table(name = "record_stall_vehicle")
public class CarInfoTable{
    @Column(name = "id", isId = true)
    private int id;
    //组键
    @Column(name = "codeId", isId = true)
    private String codeId;
    //车号
    @Column(name = "car_no")
    private String car_no;
    //车辆类型
    @Column(name = "car_type")
    private String car_type;
    //车辆所有人姓名
    @Column(name = "person_name")
    private String person_name;
    //车辆所有人电话
    @Column(name = "person_tel")
    private String person_tel;
    //车辆所有人地址
    @Column(name = "person_address")
    private String person_address;
    //有效开始时间
    @Column(name = "start_date")
    private Date start_date;
    //有效结束时间
    @Column(name = "stop_date")
    private Date stop_date;
    //数据创建时间
    @Column(name = "created_at")
    private Date created_at;
    //数据更新时间
    @Column(name = "updated_at")
    private String updated_at;
    //状态
    @Column(name = "status")
    private String status;

    @Override
    public String toString() {
        return "CarInfoTable{" +
                "id=" + id +
                ", car_no='" + car_no + '\'' +
               ", car_type='" + car_type + '\'' +
                ", person_name='" + person_name + '\'' +
                ", person_tel='" + person_tel + '\'' +
                ", person_address='" + person_address + '\'' +
                ", start_date=" + start_date +
                ", stop_date=" + stop_date +
                ", created_at=" + created_at +
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

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_tel() {
        return person_tel;
    }

    public void setPerson_tel(String person_tel) {
        this.person_tel = person_tel;
    }

    public String getPerson_address() {
        return person_address;
    }

    public void setPerson_address(String person_address) {
        this.person_address = person_address;
    }

    public Date getStop_date() {
        return stop_date;
    }

    public void setStop_date(Date stop_date) {
        this.stop_date = stop_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
