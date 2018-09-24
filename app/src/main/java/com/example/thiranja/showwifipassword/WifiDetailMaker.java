package com.example.thiranja.showwifipassword;

import android.widget.Toast;

import java.util.ArrayList;

public class WifiDetailMaker {
    private char[] fileChar;

    public WifiDetailMaker(String fileStr) {
        this.fileChar = fileStr.toCharArray();
    }

    public ArrayList<WifiDetail> makeWifiDetailObjects(){
        ArrayList<WifiDetail> data = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        WifiDetail dataModel = new WifiDetail();
        int i = 0;
        while (i != fileChar.length){
            if (fileChar[i] == '{'){
                dataModel = new WifiDetail();
            }else if(fileChar[i] == '}'){
                data.add(dataModel);
                dataModel = new WifiDetail();
            }else{
                sb.append(fileChar[i]);
                if (sb.toString().equals("ssid=")){
                    sb.delete(0,sb.length());
                    i++;
                    while(fileChar[i] != '\n'){
                        sb.append(fileChar[i]);
                        i++;
                    }
                    dataModel.setSsid(sb.toString());
                    sb.delete(0,sb.length());
                }else if (sb.toString().equals("psk=") || sb.toString().equals("wep_key0")){
                    sb.delete(0,sb.length());
                    i++;
                    while(fileChar[i] != '\n'){
                        sb.append(fileChar[i]);
                        i++;
                    }
                    dataModel.setPsk(sb.toString());
                    sb.delete(0,sb.length());
                }else if (sb.toString().equals("key_mgmt=")){
                    sb.delete(0,sb.length());
                    i++;
                    while(fileChar[i] != '\n'){
                        sb.append(fileChar[i]);
                        i++;
                    }
                    dataModel.setType(sb.toString());
                    sb.delete(0,sb.length());
                }else if (sb.toString().equals("priority=")){
                    sb.delete(0,sb.length());
                    i++;
                    while(fileChar[i] != '\n'){
                        sb.append(fileChar[i]);
                        i++;
                    }
                    dataModel.setPriority(sb.toString());
                    sb.delete(0,sb.length());
                }
                if (fileChar[i] == '\n' || fileChar[i] == ' ' || fileChar[i] == '\t'){
                    sb.delete(0,sb.length());
                }
            }
            i++;
        }
        return data;
    }


}
