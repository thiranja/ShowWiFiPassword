package com.example.thiranja.showwifipassword;

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
                dataModel.setPsk("None or Other Security Method");
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
                    if (sb.length() != 0 && sb.charAt(0) == '\"'){
                        sb.deleteCharAt(0);
                    }
                    if (sb.length() != 0 && sb.charAt(sb.length()-1) == '\"'){
                        sb.deleteCharAt(sb.length()-1);
                    }
                    dataModel.setSsid(sb.toString());
                    sb.delete(0,sb.length());
                }else if (sb.toString().equals("psk=") || sb.toString().equals("wep_key0=") || sb.toString().equals("password=")){
                    sb.delete(0,sb.length());
                    i++;
                    while(fileChar[i] != '\n'){
                        sb.append(fileChar[i]);
                        i++;
                    }
                    if (sb.length() != 0 && sb.charAt(0) == '\"'){
                        sb.deleteCharAt(0);
                    }
                    if (sb.length() != 0 && sb.charAt(sb.length()-1) == '\"'){
                        sb.deleteCharAt(sb.length()-1);
                    }
                    dataModel.setPsk(sb.toString());
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
