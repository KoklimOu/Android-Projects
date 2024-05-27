package com.okl.loginretrofit.model;


public class data {
    private Long id;
    private String userIdentity;
    private String phone;
    private boolean mdmUser;
    private boolean mdmUninstall;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isMdmUser() {
        return mdmUser;
    }

    public void setMdmUser(boolean mdmUser) {
        this.mdmUser = mdmUser;
    }

    public boolean isMdmUninstall() {
        return mdmUninstall;
    }

    public void setMdmUninstall(boolean mdmUninstall) {
        this.mdmUninstall = mdmUninstall;
    }
}