package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Endeavor on 2016/9/27 0027.
 * 收费规则表
 */
@Table(name = "money")
public class MoneyTable {

    @Column(name = "id", isId = true)
    private int id;
    //主键
    @Column(name = "fee_detail_code")
    private String fee_detail_code;
    //费用编号
    @Column(name = "fee_code")
    private String fee_code;
    //费用名称
    @Column(name = "fee_name")
    private String fee_name;
    //车型编号
    @Column(name = "car_type_code")
    private String car_type_code;
    //车型名称
    @Column(name = "car_type_name")
    private String car_type_name;
    //最小停车时长
    @Column(name = "parked_min_time")
    private int parked_min_time;
    //最大停车时长
    @Column(name = "parked_max_time")
    private int parked_max_time;
    //收费金额
    @Column(name = "money")
    private Double money;
    //创建时间
    @Column(name = "created_at")
    private Date created_at;
    //修改时间
    @Column(name = "updated_at")
    private Date updated_at;
    @Override
    public String toString() {
        return "MoneyTable{" +
                "id=" + id +
                ", fee_detail_code=" + fee_detail_code +
                ", fee_name=" + fee_name +
                ", car_type_code=" + car_type_code +
                ", car_type_name=" + car_type_name +
                ", parked_min_time=" + parked_min_time +
                ", parked_max_time=" + parked_max_time +
                ", money=" + money +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFee_detail_code() {
        return fee_detail_code;
    }

    public void setFee_detail_code(String fee_detail_code) {
        this.fee_detail_code = fee_detail_code;
    }

    public String getFee_code() {
        return fee_code;
    }

    public void setFee_code(String fee_code) {
        this.fee_code = fee_code;
    }

    public String getFee_name() {
        return fee_name;
    }

    public void setFee_name(String fee_name) {
        this.fee_name = fee_name;
    }

    public int getParked_min_time() {
        return parked_min_time;
    }

    public void setParked_min_time(int parked_min_time) {
        this.parked_min_time = parked_min_time;
    }

    public int getParked_max_time() {
        return parked_max_time;
    }

    public void setParked_max_time(int parked_max_time) {
        this.parked_max_time = parked_max_time;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
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
