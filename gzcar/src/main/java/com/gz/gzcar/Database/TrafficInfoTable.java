package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 *
 * 通行记录表
 *
 * Created by Endeavor on 2016/8/19.
 */
@Table(name = "record_in_out")
public class TrafficInfoTable {
    @Column(name = "id", isId = true)
    private int id;
    //通行记录号
    @Column(name = "pass_no")
    private String pass_no;
    //通行设备号
    @Column(name = "updated_controller_sn")
    private String updated_controller_sn;
    //车辆类型
    @Column(name = "car_type")
    private String car_type;
    //车号
    @Column(name = "car_no")
    private String car_no;
    //入场时间
    @Column(name = "in_time")
    private Date in_time;
    //入场图片
    @Column(name = "in_image")
    private String in_image;
    //入场操作员
    @Column(name = "in_user")
    private String in_user;
    //出场时间
    @Column(name = "out_time")
    private Date out_time;
    //出场图片
    @Column(name = "out_image")
    private String out_image;
    //出场操作员
    @Column(name = "out_user")
    private String out_user;
    //占用车位
    @Column(name = "stall")
    private String stall;
    //应收费用
    @Column(name = "receivable")
    private Double receivable;
    //实收费用
    @Column(name = "actual_money")
    private Double actual_money;
    //停车时长
    @Column(name = "stall_time")
    private String stall_time;
    //记录更新时间
    @Column(name = "update_time")
    private Date update_time;
    //通行状态
    @Column(name = "status")
    private String status;
    //记录修改标志
    @Column(name = "modife_flage")
    private boolean modife_flage;
    @Override
    public String toString() {
        return "TrafficInfoTable{" +
                "id=" + id +
                ", car_type='" + car_type + '\'' +
                ", car_no='" + car_no + '\'' +
                ", in_time=" + in_time +
                ", in_image='" + in_image + '\'' +
                ", in_user='" + in_user + '\'' +
                ", out_time=" + out_time +
                ", out_image='" + out_image + '\'' +
                ", out_user='" + out_user + '\'' +
                ", receivable=" + receivable +
                ", actual_money=" + actual_money +
                ", stall=" + stall +
                ", stall_time=" + stall_time +
                ", update_time=" + update_time +
                ", modife_flage=" + modife_flage +
                ", updated_controller_sn=" + updated_controller_sn +
        '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCar_type() {
        return car_type;
    }

    public String getPass_no() {        return pass_no;    }

    public void setPass_no(String pass_no) {        this.pass_no = pass_no;    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public Date getIn_time() {
        return in_time;
    }

    public void setIn_time(Date in_time) {
        this.in_time = in_time;
    }

    public String getIn_image() {
        return in_image;
    }

    public void setIn_image(String in_image) {
        this.in_image = in_image;
    }

    public String getIn_user() {        return in_user;    }

    public void setIn_user(String in_user) {        this.in_user = in_user;    }


    public Date getOut_time() {
        return out_time;
    }

    public void setOut_time(Date out_time) {
        this.out_time = out_time;
    }

    public String getOut_image() {
        return out_image;
    }

    public void setOut_image(String out_image) {
        this.out_image = out_image;
    }

    public String getOut_user() {        return out_user;    }

    public void setOut_user(String out_user) {        this.out_user = out_user;    }


    public Date getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(Date updateTime) {
        this.update_time = updateTime;
    }
    public Double getReceivable() {    return receivable;    }


    public String getStall() {        return stall;    }

    public void setStall(String stall) {        this.stall = stall;    }

    public String getStall_time() {        return stall_time;    }

    public void setStall_time(String stall_time) {this.stall_time = stall_time;}

    public void setReceivable(Double receivable) {    this.receivable = receivable;    }

    public Double getActual_money() {        return actual_money;    }

    public void setActual_money(Double actual_money) {        this.actual_money = actual_money;    }

    public String getStatus() {        return status;    }

    public void setStatus(String status) {        this.status = status;    }

    public boolean isModifeFlage() {        return modife_flage;    }

    public void setModifeFlage(boolean modife_flage) {        this.modife_flage = modife_flage;    }

    public String getUpdated_controller_sn() {        return updated_controller_sn;    }

    public void setUpdated_controller_sn(String updated_controller_sn) {        this.updated_controller_sn = updated_controller_sn;    }
}
