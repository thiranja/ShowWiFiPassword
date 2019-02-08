package com.myapp.thiranja.showwifipassword;

import java.util.ArrayList;

class WifiDetailMakerForO {

    private char[] fileChar;
    private ArrayList<WifiDetail> data;

    WifiDetailMakerForO(String fileStr, ArrayList<WifiDetail> data) {
        this.data = data;
        this.fileChar = fileStr.toCharArray();
    }

    void makeWifiDetailObjects(){
        StringBuilder sb = new StringBuilder();
        WifiDetail dataModel = new WifiDetail();

        int i = 0;
        int j;

        while (i != fileChar.length){

            if (fileChar[i] != '\n') {
                sb.append(fileChar[i]);
            }
            switch (sb.toString()) {
                case "<WifiConfiguration>":
                    dataModel = new WifiDetail();
                    dataModel.setPsk("None");

                    break;
                case "</WifiConfiguration>":
                    data.add(dataModel);
                    dataModel = new WifiDetail();

                    break;
                // code for extracting ssid
                case "<string name=\"SSID\">":
                    sb.delete(0, sb.length());
                    i += 7;
                    while (fileChar[i] == '\n') {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        i++;
                    }
                    j = i + 1;
                    while ((fileChar[i] != '&' || fileChar[j] != 'q')) {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        if (fileChar[i] != '\n') {
                            sb.append(fileChar[i]);
                        }
                        if (fileChar[i] == '&') {
                            i += 4;
                        }
                        i++;
                        j = i + 1;
                        if (fileChar[j] == '&' && fileChar[j + 1] == '\n') {
                            j++;
                        }
                    }

                    dataModel.setSsid(sb.toString());
                    sb.delete(0, sb.length());
                    break;
                //code for extracting wpa passwords
                case "<string name=\"PreSharedKey\">":
                    sb.delete(0, sb.length());
                    i += 7;
                    while (fileChar[i] == '\n') {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        i++;
                    }
                    j = i + 1;
                    while ((fileChar[i] != '&' || fileChar[j] != 'q')) {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        if (fileChar[i] != '\n') {
                            sb.append(fileChar[i]);
                        }
                        if (fileChar[i] == '&') {
                            i += 4;
                        }
                        i++;
                        j = i + 1;
                        if (fileChar[j] == '&' && fileChar[j + 1] == '\n') {
                            j++;
                        }
                    }

                    dataModel.setPsk(sb.toString());
                    sb.delete(0, sb.length());

                    break;
                // Code for extracting wep password
                case "<item value=\"&quot;":
                    sb.delete(0, sb.length());
                    i++;
                    while (fileChar[i] == '\n') {
                        // for safety
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        i++;
                    }
                    j = i + 1;
                    while ((fileChar[i] != '&' || fileChar[j] != 'q')) {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        if (fileChar[i] != '\n') {
                            sb.append(fileChar[i]);
                        }
                        if (fileChar[i] == '&') {
                            i += 4;
                        }
                        i++;
                        j = i + 1;
                        if (fileChar[j] == '&' && fileChar[j + 1] == '\n') {
                            j++;
                        }
                    }
                    dataModel.setPsk(sb.toString());
                    sb.delete(0, sb.length());
                    break;
                // Code for Getting password 802.EAP passwords
                case "<string name=\"Password\">":
                    sb.delete(0, sb.length());
                    i++;
                    while (fileChar[i] == '\n') {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        i++;
                    }
                    j = i + 1;
                    while ((fileChar[i] != '<' || fileChar[j] != '/')) {
                        if (i == fileChar.length - 10) {
                            break;
                        }
                        if (fileChar[i] != '\n') {
                            sb.append(fileChar[i]);
                        }
                        if (fileChar[i] == '&') {
                            i += 4;
                        }
                        i++;
                        j = i + 1;
                        if (fileChar[j] == '&' && fileChar[j + 1] == '\n') {
                            j++;
                        }
                    }

                    if (!data.isEmpty()) {
                        if (data.get(data.size() - 1).getPsk() != null) {
                            data.get(data.size() - 1).setPsk(sb.toString());
                        }
                    }
                    sb.delete(0, sb.length());

                    break;
            }

            if (fileChar[i] == '>'){
                sb.delete(0,sb.length());
            }
            i++;
        }
    }
}
