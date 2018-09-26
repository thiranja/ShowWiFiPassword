package com.example.thiranja.showwifipassword;

public class WifiDetail {

    private String ssid;
    private String psk;

    public WifiDetail(String ssid, String psk, String type, String priority) {
        this.ssid = ssid;
        this.psk = psk;

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

}
