package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 *
 * 通行记录表
 * 2016-09-26
 * Created by Endeavor on 2016/8/19.
 */
@Table(name = "record_in_out")
public class TrafficInfoTable {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "pass_no")
    private String pass_no;

    @Column(name = "card_type")
    private String card_type;

    @Column(name = "car_no")
    private String car_no;

    @Column(name = "in_time")
    private Date in_time;

    @Column(name = "in_door")
    private String in_door;

    @Column(name = "in_image")
    private String in_image;

    @Column(name = "Status")
    private String Status;

    @Column(name = "out_time")
    private Date out_time;

    @Column(name = "out_door")
    private String out_door;

    @Column(name = "out_image")
    private String out_image;

    @Column(name = "part_time")
    private String part_time;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "fee")
    private String fee;

    @Column(name = "end_date")
    private String end_date;

    @Column(name = "person_address")
    private String person_address;

    @Column(name = "created_at_var")
    private String created_at_var;

    @Column(name = "in_time_var")
    private String in_time_var;

    @Column(name = "out_time_var")
    private Date out_time_var;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "modife_flage")
    private boolean modifeFlage;

    @Column(name = "user_name")
    private String userName;

    @Override
    public String toString() {
        return "TrafficInfoTable{" +
                "id=" + id +
                ", pass_no='" + pass_no + '\'' +
                ", card_type='" + card_type + '\'' +
                ", car_no='" + car_no + '\'' +
                ", in_time=" + in_time +
                ", in_door='" + in_door + '\'' +
                ", in_image='" + in_image + '\'' +
                ", Status='" + Status + '\'' +
                ", out_time=" + out_time +
                ", out_door='" + out_door + '\'' +
                ", out_image='" + out_image + '\'' +
                ", part_time='" + part_time + '\'' +
                ", created_at=" + created_at +
                ", fee='" + fee + '\'' +
                ", end_date='" + end_date + '\'' +
                ", person_address='" + person_address + '\'' +
                ", created_at_var='" + created_at_var + '\'' +
                ", in_time_var='" + in_time_var + '\'' +
                ", out_time_var=" + out_time_var +
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

    public String getPass_no() {
        return pass_no;
    }

    public void setPass_no(String pass_no) {
        this.pass_no = pass_no;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
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

    public String getIn_door() {
        return in_door;
    }

    public void setIn_door(String in_door) {
        this.in_door = in_door;
    }

    public String getIn_image() {
        return in_image;
    }

    public void setIn_image(String in_image) {
        this.in_image = in_image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Date getOut_time() {
        return out_time;
    }

    public void setOut_time(Date out_time) {
        this.out_time = out_time;
    }

    public String getOut_door() {
        return out_door;
    }

    public void setOut_door(String out_door) {
        this.out_door = out_door;
    }

    public String getOut_image() {
        return out_image;
    }

    public void setOut_image(String out_image) {
        this.out_image = out_image;
    }

    public String getPart_time() {
        return part_time;
    }

    public void setPart_time(String part_time) {
        this.part_time = part_time;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPerson_address() {
        return person_address;
    }

    public void setPerson_address(String person_address) {
        this.person_address = person_address;
    }

    public String getCreated_at_var() {
        return created_at_var;
    }

    public void setCreated_at_var(String created_at_var) {
        this.created_at_var = created_at_var;
    }

    public String getIn_time_var() {
        return in_time_var;
    }

    public void setIn_time_var(String in_time_var) {
        this.in_time_var = in_time_var;
    }

    public Date getOut_time_var() {
        return out_time_var;
    }

    public void setOut_time_var(Date out_time_var) {
        this.out_time_var = out_time_var;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getModifeFlage() {
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
