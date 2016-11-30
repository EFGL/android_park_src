package com.gz.gzcar.Database;

import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.L;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.util.Date;

/**
 * 车辆信息信息表
 * <p>
 * Created by Endeavor on 2016/8/18.
 */

@Table(name = "record_stall_vehicle")
public class CarInfoTable {


    @Column(name = "id", isId = true)
    private int id;
    //组键
    @Column(name = "codeId")
    private String codeId;
    //车号
    @Column(name = "car_no")
    private String car_no;
    //固定车类型  月租或年租之类
    @Column(name = "car_type")
    private String car_type;
    //车辆类型 固定车 或者 特殊车
    @Column(name = "vehicle_type")
    private String vehicle_type;
    //收费类型
    @Column(name = "fee_type")
    private String fee_type;
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
    //有效次数
    @Column(name = "allow_count")
    private int allow_count;
    //免费时长（分钟）
    @Column(name = "allow_park_time")
    private int allow_park_time;
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
                ", vehicle_type='" + vehicle_type + '\'' +
                ", fee_type='" + fee_type + '\'' +
                ", person_name='" + person_name + '\'' +
                ", person_tel='" + person_tel + '\'' +
                ", person_address='" + person_address + '\'' +
                ", start_date=" + start_date +
                ", stop_date=" + stop_date +
                ", allow_count=" + allow_count +
                ", allow_park_time=" + allow_park_time +
                ", created_at=" + created_at +
                ", updated_at='" + updated_at + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public CarInfoTable() {
    }

    public CarInfoTable(String[] members) throws DbException {

        if (!members[1].isEmpty())
            this.codeId = members[1];
        if (!members[2].isEmpty())
            this.car_no = members[2];
        if (!members[3].isEmpty())
            this.car_type = members[3];
        if (!members[4].isEmpty())
            this.vehicle_type = members[4];
        if (!members[5].isEmpty())
            this.fee_type = members[5];
        if (!members[6].isEmpty())
            this.person_name = members[6];
        if (!members[7].isEmpty())
            this.person_tel = members[7];
        if (!members[8].isEmpty())
            this.person_address = members[8];
        if (!members[9].isEmpty()){
         /*   try {
                this.start_date = new SimpleDateFormat("yyyy/MM/dd").parse(members[9]);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            this.start_date = DateUtils.string2Date(members[9]);
        }
        if (!members[10].isEmpty()){

           /* try {
                this.stop_date = new SimpleDateFormat("yyyy/MM/dd").parse(members[10]);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            this.stop_date = DateUtils.string2Date(members[10]);
        }
        if (!members[11].isEmpty())
            this.allow_count = Integer.parseInt(members[11]);
        if (!members[12].isEmpty())
            this.allow_park_time = Integer.parseInt(members[12]);
        L.showlogError("12=="+members[12]);
        if (!members[13].isEmpty())
            this.created_at = DateUtils.string2Date(members[13]);
        if (!members[14].isEmpty())
            this.updated_at = members[14];
        if (!members[15].isEmpty())
            this.status = members[15];
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

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
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

    public int getAllow_count() {
        return allow_count;
    }

    public void setAllow_count(int allow_count) {
        this.allow_count = allow_count;
    }

    public int getAllow_park_time() {
        return allow_park_time;
    }

    public void setAllow_park_time(int allow_park_time) {
        this.allow_park_time = allow_park_time;
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
