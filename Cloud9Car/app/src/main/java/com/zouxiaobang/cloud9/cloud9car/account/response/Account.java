package com.zouxiaobang.cloud9.cloud9car.account.response;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class Account {
    private String token;
    private String uid;
    private String account;
    /**
     * 有效时间
     */
    private long expired;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }
}
