package com.example.doan.Models;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {

    @SerializedName("phone")
    private String phone;

    @SerializedName("otp")
    private String otp;

    public OtpRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
