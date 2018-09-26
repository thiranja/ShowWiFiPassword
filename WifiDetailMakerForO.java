package com.example.thiranja.showwifipassword;

import java.util.ArrayList;

public class WifiDetailMakerForO {

    private char[] fileChar;

    public WifiDetailMakerForO(String fileStr) {
        this.fileChar = fileStr.toCharArray();
    }

    public ArrayList<WifiDetail>makeWifiDetailObjects(){
        ArrayList<WifiDetail> data = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        WifiDetail dataModel = new WifiDetail();

        int i = 0;
        int j;

        while (i != fileChar.length){

            if (fileChar[i] != '\n') {
                sb.append(fileChar[i]);
            }
            if (sb.toString().equals("<WifiConfiguration>")){
                dataModel = new WifiDetail();
                dataModel.setPsk("None or Other Security Method");

            }else if (sb.toString().equals("</WifiConfiguration>")){
                data.add(dataModel);
                dataModel = new WifiDetail();

            }
            // code for extracting ssid
            else if (sb.toString().equals("<string name=\"SSID\">")){
                sb.delete(0,sb.length());
                i += 7;
                while (fileChar[i] == '\n'){
                    i++;
                }
                j = i + 1;
                while ((fileChar[i] != '&' || fileChar[j] != 'q')) {
                    if (fileChar[i] != '\n') {
                        sb.append(fileChar[i]);
                    }
                    if (fileChar[i] == '&'){
                        i += 4;
                    }
                    i++;
                    j = i + 1;
                    if (fileChar[j] == '&' && fileChar[j+1]=='\n'){
                        j++;
                    }
                }

                dataModel.setSsid(sb.toString());
                sb.delete(0,sb.length());
            }
            //code for extracting wpa passwords
            else if (sb.toString().equals("<string name=\"PreSharedKey\">")){
                sb.delete(0,sb.length());
                i += 7;
                while (fileChar[i] == '\n'){
                    i++;
                }
                j = i + 1;
                while ((fileChar[i] != '&' || fileChar[j] != 'q')) {
                    if (fileChar[i] != '\n') {
                        sb.append(fileChar[i]);
                    }
                    if (fileChar[i] == '&'){
                        i += 4;
                    }
                    i++;
                    j = i + 1;
                    if (fileChar[j] == '&' && fileChar[j+1]=='\n'){
                        j++;
                    }
                }

                dataModel.setPsk(sb.toString());
                sb.delete(0,sb.length());

            }
            // Code for extracting wep password
            else if(sb.toString().equals("<item value=\"&quot;")){
                sb.delete(0,sb.length());
                i++;
                while (fileChar[i] == '\n'){
                    i++;
                }
                j = i + 1;
                while ((fileChar[i] != '&' || fileChar[j] != 'q')) {
                    if (fileChar[i] != '\n') {
                        sb.append(fileChar[i]);
                    }
                    if (fileChar[i] == '&'){
                        i += 4;
                    }
                    i++;
                    j = i + 1;
                    if (fileChar[j] == '&' && fileChar[j+1]=='\n'){
                        j++;
                    }
                }
                dataModel.setPsk(sb.toString());
                sb.delete(0,sb.length());
            }

            if (fileChar[i] == '>'){
                sb.delete(0,sb.length());
            }
            i++;
        }
        return data;
    }
}
