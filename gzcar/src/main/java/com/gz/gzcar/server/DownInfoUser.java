package com.gz.gzcar.server;

import org.xutils.db.annotation.Column;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class DownInfoUser {
    private int id;
    private String login;
    private String password;
    private String user_type;
    private String status;
    private Date created_at;
    private Date updated_at;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {  return login;    }

    public void setLogin(String login) {        this.login = login;    }

    public String getUser_type() {        return user_type;    }

    public void setUser_type(String user_type) {        this.user_type = user_type;    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getStatus() {        return status;    }

    public void setStatus(String status) {       this.status = status;    }

    public Date getCreated_at() {        return created_at;    }

    public void setCreated_at(Date created_at) {        this.created_at = created_at;    }

    public Date getUpdated_at() {        return updated_at;    }

    public void setUpdated_at(Date updated_at) {        this.updated_at = updated_at;    }
}
