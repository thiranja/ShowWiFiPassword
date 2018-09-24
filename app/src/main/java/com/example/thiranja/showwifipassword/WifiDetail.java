package com.example.thiranja.showwifipassword;

public class WifiDetail {

    private String ssid;
    private String psk;
    private String type;
    private String priority;

    public WifiDetail(String ssid, String psk, String type, String priority) {
        this.ssid = ssid;
        this.psk = psk;
        this.type = type;
        this.priority = priority;
    }

    public WifiDetail() { }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPsk() {
        return psk;
    }

    public void setPsk(String psk) {
        this.psk = psk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
