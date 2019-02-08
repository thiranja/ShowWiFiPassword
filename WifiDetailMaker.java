package com.myapp.thiranja.showwifipassword;

import java.util.ArrayList;

class WifiDetailMaker {
    private char[] fileChar;
    private ArrayList<WifiDetail> data;

    WifiDetailMaker(String fileStr, ArrayList<WifiDetail> data) {
        this.data = data;
        this.fileChar = fileStr.toCharArray();
    }

    void makeWifiDetailObjects(){
        StringBuilder sb = new StringBuilder();
        WifiDetail dataModel = new WifiDetail();
        int i = 0;
        while (i != fileChar.length){
            if (fileChar[i] == '{'){
                dataModel = new WifiDetail();
                dataModel.setPsk("None");
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
    }


}
