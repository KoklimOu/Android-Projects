package com.okl.loginretrofit.remote;


import com.okl.loginretrofit.model.data;

/**
 *<p>Modify this class according to your Login API's response JSON.</p>
 **/
public class MyResponse {
    public String status;
    public data data;
    public String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.okl.loginretrofit.model.data getData() {
        return data;
    }

    public void setData(com.okl.loginretrofit.model.data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
