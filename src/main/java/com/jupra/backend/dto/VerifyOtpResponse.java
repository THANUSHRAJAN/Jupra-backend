package com.jupra.backend.dto;

public class VerifyOtpResponse {

    private String resetToken;

    public VerifyOtpResponse() {
    }

    public VerifyOtpResponse(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
