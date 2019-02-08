package com.myapp.thiranja.showwifipassword;

import android.os.Build;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

class WifiFileExtractor {

    private int sdk_int = Build.VERSION.SDK_INT;

    String returnInString(){
        String file = "";
        // Tigering the supper user permission on the device and granting it
        boolean suEh = Shell.SU.available();
        if (suEh){
            // Getting the values as a String list
            List<String> supplicant;
            if (sdk_int >= Build.VERSION_CODES.O){
                supplicant = Shell.SU.run("cat /data/misc/wifi/WifiConfigStore.xml");
            }else {
                supplicant = Shell.SU.run("cat /data/misc/wifi/wpa_supplicant.conf");
            }
            // Making the string list a single string
            StringBuilder lineAdder = new StringBuilder();
            if (supplicant != null){
                for (String line: supplicant){
                    lineAdder.append(line);
                    lineAdder.append('\n');
                }
            }
            file = lineAdder.toString();
        }/*else{
            // This is just a try to retrieve data from non-rooted devices
            // it may works for some devices

        }*/
        return file;
    }
}
