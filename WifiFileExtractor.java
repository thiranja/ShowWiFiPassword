package com.example.thiranja.showwifipassword;

import android.os.Build;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class WifiFileExtractor {

    private boolean suEh;
    List<String> supplicant;
    private int sdk_int = Build.VERSION.SDK_INT;

    public String returnInString(){
        String file = "";
        // Tiggering the supper user permission on the divice and granting it
        suEh = Shell.SU.available();
        if (suEh){
            // Getting the values as a String list
            if (sdk_int >= Build.VERSION_CODES.O){
                supplicant = Shell.SU.run("cat /data/misc/wifi/WifiConfigStore.xml");
            }else {
                supplicant = Shell.SU.run("cat /data/misc/wifi/wpa_supplicant.conf");
            }
            // Making the string list a single string
            StringBuilder lineAdder = new StringBuilder();
            if (supplicant != null){
                for (String line:supplicant){
                    lineAdder.append(line);
                    lineAdder.append('\n');
                }
            }
            file = lineAdder.toString();
        }
        return file;
    }
}
