package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * 车辆信息信息表
 * <p/>
 * Created by Endeavor on 2016/8/18.
 */

@Table(name = "record_stall_vehicle")
public class CarInfoTable implements Comparable {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "carId")
    private String carId;

    @Column(name = "fee_flag")
    private String fee_flag;

    @Column(name = "garage_code")
    private String garage_code;

    @Column(name = "note")
    private String note;

    @Column(name = "car_no")
    private String car_no;

    @Column(name = "car_wei")
    private String carWei;

    @Column(name = "updated_at")
    private String updated_at;

    @Column(name = "car_type")
    private String car_type;

    @Column(name = "card_no")
    private String card_no;

    @Column(name = "label_no")
    private String label_no;

    @Column(name = "person_name")
    private String person_name;

    @Column(name = "person_sex")
    private String person_sex;

    @Column(name = "person_tel")
    private String person_tel;

    @Column(name = "person_address")
    private String person_address;

    @Column(name = "person_idcard")
    private String person_idcard;

    @Column(name = "car_color")
    private String car_color;

    @Column(name = "stop_date")
    private Date stop_date;

    @Column(name = "start_date")
    private Date start_date;


    @Column(name = "car_image")
    private String car_image;


    @Column(name = "created_at")
    private Date created_at;


    @Column(name = "status")
    private String status;


    @Column(name = "park_code")
    private String park_code;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "modife_flage")
    private boolean modifeFlage;

    @Column(name = "user_name")
    private String userName;

    @Override
    public int compareTo(Object andother) {

        return ((CarInfoTable) andother).getId() - this.id;
    }

    @Override
    public String toString() {
        return "CarInfoTable{" +
                "id=" + id +
                ", carId='" + carId + '\'' +
                ", fee_flag='" + fee_flag + '\'' +
                ", garage_code='" + garage_code + '\'' +
                ", note='" + note + '\'' +
                ", car_no='" + car_no + '\'' +
                ", carWei='" + carWei + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", car_type='" + car_type + '\'' +
                ", card_no='" + card_no + '\'' +
                ", label_no='" + label_no + '\'' +
                ", person_name='" + person_name + '\'' +
                ", person_sex='" + person_sex + '\'' +
                ", person_tel='" + person_tel + '\'' +
                ", person_address='" + person_address + '\'' +
                ", person_idcard='" + person_idcard + '\'' +
                ", car_color='" + car_color + '\'' +
                ", stop_date=" + stop_date +
                ", start_date=" + start_date +
                ", car_image='" + car_image + '\'' +
                ", created_at=" + created_at +
                ", status='" + status + '\'' +
                ", park_code='" + park_code + '\'' +
                ", updateTime=" + updateTime +
                ", modifeFlage=" + modifeFlage +
                ", userName='" + userName + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getFee_flag() {
        return fee_flag;
    }

    public void setFee_flag(String fee_flag) {
        this.fee_flag = fee_flag;
    }

    public String getGarage_code() {
        return garage_code;
    }

    public void setGarage_code(String garage_code) {
        this.garage_code = garage_code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getCarWei() {
        return carWei;
    }

    public void setCarWei(String carWei) {
        this.carWei = carWei;
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

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getLabel_no() {
        return label_no;
    }

    public void setLabel_no(String label_no) {
        this.label_no = label_no;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_sex() {
        return person_sex;
    }

    public void setPerson_sex(String person_sex) {
        this.person_sex = person_sex;
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

    public String getPerson_idcard() {
        return person_idcard;
    }

    public void setPerson_idcard(String person_idcard) {
        this.person_idcard = person_idcard;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
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

    public String getCar_image() {
        return car_image;
    }

    public void setCar_image(String car_image) {
        this.car_image = car_image;
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

    public String getPark_code() {
        return park_code;
    }

    public void setPark_code(String park_code) {
        this.park_code = park_code;
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
}
