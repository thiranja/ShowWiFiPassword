package com.example.thiranja.showwifipassword;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private int sdk_int = Build.VERSION.SDK_INT;

    ArrayList<WifiDetail> data;
    ListView listView;
    private static CustomAdapter adapter;

    private ShareActionProvider mShareActionProvider;

    private LinearLayout conNet;
    private boolean conNetAvailable = false;

    private TextView conSSID;
    private TextView conPSK;
    private String conSSIDStr;

    private AdView bottomBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the mobile ads sdk in the app
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        listView= findViewById(R.id.list);

        // Connecting the layout with activity
        conNet = findViewById(R.id.conected_network_layout);
        conSSID = findViewById(R.id.cur_ssid);
        conPSK = findViewById(R.id.cur_psk);
        conSSIDStr = null;

        // Extracting the wifi supplicant file and getting it as a String
        WifiFileExtractor extractor = new WifiFileExtractor();
        final String fileStr = extractor.returnInString();

        // Extracting relavant components from wifi supplicant data and making the
        // wifi detail object arraylist
        data = new ArrayList<>();
        if (sdk_int >= Build.VERSION_CODES.O){
            WifiDetailMakerForO makerForO = new WifiDetailMakerForO(fileStr);
            data = makerForO.makeWifiDetailObjects();
        }else {
            WifiDetailMaker maker = new WifiDetailMaker(fileStr);
            data = maker.makeWifiDetailObjects();
        }

        // resourcing the custom adapter
        adapter= new CustomAdapter(data,getApplicationContext());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                WifiDetail dataModel= data.get(position);

                StringBuilder dataMaker = new StringBuilder();
                dataMaker.append("SSID = ");
                dataMaker.append(dataModel.getSsid());
                dataMaker.append('\n');

                dataMaker.append("PSK = ");
                dataMaker.append(dataModel.getPsk());
                dataMaker.append('\n');

                String message = dataMaker.toString();

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = message;
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WiFi Access Delivary");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(intent, "Choose sharing method"));

            }
        });

        conNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder dataMaker = new StringBuilder();
                dataMaker.append("SSID = ");
                dataMaker.append(conSSID.getText().toString());
                dataMaker.append('\n');

                dataMaker.append("PSK = ");
                dataMaker.append(conPSK.getText().toString());
                dataMaker.append('\n');

                String message = dataMaker.toString();

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = message;
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WiFi Access Delivary");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(intent, "Choose sharing method"));
            }
        });

        // Making the ad view visible in the app
        bottomBanner = findViewById(R.id.bottom_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bottomBanner.loadAd(adRequest);
        bottomBanner.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                bottomBanner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                bottomBanner.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        bottomBanner.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForConnectionDetails();
        currentConnectionLayoutInitiator();
        bottomBanner.resume();
    }

    @Override
    protected void onDestroy() {
        if (bottomBanner != null) {
            bottomBanner.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)){
                    adapter.filter("");
                    listView.clearTextFilter();
                    if (conNetAvailable) {
                        conNet.setVisibility(View.VISIBLE);
                    }
                }else{
                    adapter.filter(s);
                    if (conNetAvailable) {
                        conNet.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });
        return true;
    }

    public void checkForConnectionDetails(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean permissionGranted;
        if (sdk_int >= Build.VERSION_CODES.O){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){

                permissionGranted = false;
                //Permission Not Granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                permissionGranted = true;
            }
        }else{
            permissionGranted = true;
        }
        if (wifiManager.isWifiEnabled() && permissionGranted) {
            WifiInfo wifiInfo;

            wifiInfo = wifiManager.getConnectionInfo();

            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                conSSIDStr = wifiInfo.getSSID();
            }
            if (conSSIDStr != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(conSSIDStr);
                if (sb.length() > 2) {
                    if (sb.charAt(0) == '\"') {
                        sb.deleteCharAt(0);
                    }
                    if (sb.charAt(sb.length() - 1) == '\"') {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                conSSIDStr = sb.toString();
                conNetAvailable = true;
            } else {
                conNetAvailable = false;
            }

        }else{
            conNetAvailable = false;
        }
    }

    public void currentConnectionLayoutInitiator(){

        if (conNetAvailable){
            WifiDetail curWifi = getCurWifiDetail(conSSIDStr);
            conSSID.setText(curWifi.getSsid());
            conPSK.setText(curWifi.getPsk());
            conNet.setVisibility(View.VISIBLE);
        }else{
            conNet.setVisibility(View.GONE);
        }
    }

    public WifiDetail getCurWifiDetail(String ssid){
        WifiDetail detail = null;
        if (data != null){
            for (WifiDetail dataModel:data){
                if (dataModel.getSsid().equals(ssid)){
                    detail = dataModel;
                    break;
                }
            }
        }else{
            detail = new WifiDetail();
            detail.setSsid(conSSIDStr);
            detail.setPsk("No Clue");
        }
        return detail;
    }
}
