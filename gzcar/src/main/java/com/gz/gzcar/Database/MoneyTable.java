package com.gz.gzcar.Database;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Endeavor on 2016/9/27 0027.
 * 收费规则表
 */
@Table(name = "money")
public class MoneyTable {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "part_time")
    private Double partTime;


    @Column(name = "money")
    private Double money;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getPartTime() {
        return partTime;
    }

    public void setPartTime(Double partTime) {
        this.partTime = partTime;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "MoneyTable{" +
                "id=" + id +
                ", partTime=" + partTime +
                ", money=" + money +
                '}';
    }
}
