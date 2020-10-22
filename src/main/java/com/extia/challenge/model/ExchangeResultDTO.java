package com.extia.challenge.model;

public class ExchangeResultDTO {

    private String parity;
    private String details;
    private String date;

    public String getParity() {
        return parity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }
    
    
    
}