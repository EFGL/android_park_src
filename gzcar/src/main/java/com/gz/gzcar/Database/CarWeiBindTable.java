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

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "modife_flage")
    private boolean modifeFlage;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "car_number")
    private String carNumber;

    @Column(name = "car_wei")
    private String carWei;


    @Override
    public String toString() {
        return "CarWeiBindTable{" +
                "id=" + id +
                ", updateTime=" + updateTime +
                ", modifeFlage=" + modifeFlage +
                ", userName='" + userName + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", carWei='" + carWei + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isModifeFlage() {
        return modifeFlage;
    }

    public void setModifeFlage(boolean modifeFlage) {
        this.modifeFlage = modifeFlage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarWei() {
        return carWei;
    }

    public void setCarWei(String carWei) {
        this.carWei = carWei;
    }
}
