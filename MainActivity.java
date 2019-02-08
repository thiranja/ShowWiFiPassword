package com.myapp.thiranja.showwifipassword;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity{

    private int sdk_int = Build.VERSION.SDK_INT;

    ArrayList<WifiDetail> data;
    ListView listView;
    private CustomAdapter adapter;

    private LinearLayout conNet;
    private LinearLayout noRoot;
    private LinearLayout noRootHelpLayout;
    private boolean isRooted = false;
    private boolean conNetAvailable = true;

    private TextView conSSID;
    private TextView conPSK;
    private String conSSIDStr;
    private String conPSKStr = "Searching";

    private AdView bottomBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initializing the mobile ads sdk in the app
        MobileAds.initialize(this,"ca-app-pub-3824216403455651~2159606475");

        listView= findViewById(R.id.list);

        // Connecting the layout with activity
        conNet = findViewById(R.id.conected_network_layout);
        noRoot = findViewById(R.id.not_root_layout);
        conSSID = findViewById(R.id.cur_ssid);
        conPSK = findViewById(R.id.cur_psk);
        conSSIDStr = "Searching";



        data = new ArrayList<>();

        adapter= new CustomAdapter(data,getApplicationContext());

        // resourcing the custom adapter
        listView.setAdapter(adapter);

        // Executing the wifi data fetching tasks in a background thread
        new WifiDataFetcher().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                WifiDetail dataModel= data.get(position);

                String message = "SSID = " +
                        dataModel.getSsid() +
                        '\n' +
                        "PSK = " +
                        dataModel.getPsk() +
                        '\n';

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_wifi_sub);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_method)));

            }
        });

        conNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = "SSID = " +
                        conSSID.getText().toString() +
                        '\n' +
                        "PSK = " +
                        conPSK.getText().toString() +
                        '\n';
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_wifi_sub);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_method)));
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

        // App rate alert box
        AppRate.with(this)
                .setInstallDays(3)
                .setRemindInterval(3)
                .setLaunchTimes(7)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

        //Making No Root mode Actions

        Button noRootMode = findViewById(R.id.no_root_mode_btn);
        TextView noRootModeHelp = findViewById(R.id.no_root_mode_help_btn);
        noRootHelpLayout = findViewById(R.id.no_root_mode_help_layout);

        noRootMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (conNetAvailable){
                    String gateway = getGateway();
                    Intent noRootModeActivity = new Intent(MainActivity.this,NoRootModeActivity.class);
                    noRootModeActivity.putExtra("GATEWAY",gateway);
                    startActivity(noRootModeActivity);
                }else{
                    Toast.makeText(MainActivity.this, "Need to be Connected to the WiFi Network", Toast.LENGTH_SHORT).show();
                }

            }
        });

        noRootModeHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noRootHelpLayout.getVisibility() == View.GONE){
                    noRootHelpLayout.setVisibility(View.VISIBLE);
                }else{
                    noRootHelpLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public WifiDetail getCurWifiDetail(String ssid){
        WifiDetail detail = null;
        boolean found = false;
        if (!data.isEmpty()){
            for (WifiDetail dataModel:data){
                if (dataModel.getSsid().equals(ssid)){
                    detail = dataModel;
                    found = true;
                    break;
                }
            }
        }
        if (!found){
            detail = new WifiDetail();
            detail.setSsid(conSSIDStr);
            detail.setPsk(conPSKStr);
        }
        return detail;
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
                    if (conNetAvailable && isRooted) {
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

        // Share app funcionality
        MenuItem shareApp = menu.findItem(R.id.share_app);
        shareApp.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String message = getString(R.string.app_url);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_share_subject);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_method)));
                return false;
            }
        });

        // No root Mode
        MenuItem noRootMode = menu.findItem(R.id.no_root_mode_munu);
        noRootMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (conNetAvailable){
                    String gateway = getGateway();
                    Intent noRootModeActivity = new Intent(MainActivity.this,NoRootModeActivity.class);
                    noRootModeActivity.putExtra("GATEWAY",gateway);
                    startActivity(noRootModeActivity);
                }else{
                    Toast.makeText(MainActivity.this, "Need to be Connected to the WiFi Network", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // Help pop up funtionality
        MenuItem help = menu.findItem(R.id.help_menu);
        help.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent helpActivity = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(helpActivity);
                return false;
            }
        });

        MenuItem writeFile = menu.findItem(R.id.export_file);
        writeFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                exportToFile();
                return false;
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
        if (wifiManager == null) {
            throw new AssertionError();
        }
        if (wifiManager.isWifiEnabled() && permissionGranted) {
            WifiInfo wifiInfo;

            wifiInfo = wifiManager.getConnectionInfo();

            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                conSSIDStr = wifiInfo.getSSID();
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
        }else{
            conNetAvailable = false;
        }
    }

    public void currentConnectionLayoutInitiator(){

        if (isRooted) {
            noRoot.setVisibility(View.GONE);
            if (conNetAvailable) {
                WifiDetail curWifi = getCurWifiDetail(conSSIDStr);
                conSSID.setText(curWifi.getSsid());
                conPSK.setText(curWifi.getPsk());
                conNet.setVisibility(View.VISIBLE);
            }else{
                conNet.setVisibility(View.GONE);
            }
        }else{
            conNet.setVisibility(View.GONE);
            noRoot.setVisibility(View.VISIBLE);
        }
    }

    public String getGateway(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int gate = dhcpInfo.gateway;
        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            gate = Integer.reverseBytes(gate);
        }
        String gateway = "Unknown";
        try {
            gateway = "http://"+InetAddress.getByAddress(BigInteger.valueOf(gate).toByteArray()).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return gateway;
    }

    public void exportToFile(){
        boolean permissionGranted;
        if (sdk_int >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){

                permissionGranted = false;
                //Permission Not Granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
            }else{
                permissionGranted = true;
            }
        }else{
            permissionGranted = true;
        }
        if (permissionGranted){
            if (data.isEmpty()){
                Toast.makeText(this, "Network List is Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            String content = writableWiFiPasswordStringMaker();
            FileWriter fileWriter = new FileWriter();
            boolean wasWritten = fileWriter.WriteToFile("WiFiPasswordList.txt",content);
            if (wasWritten){
                Toast.makeText(MainActivity.this, "File Successfully Written", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Saved to Documents", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Permission Denied for Writing Files", Toast.LENGTH_SHORT).show();
        }
    }

    public String writableWiFiPasswordStringMaker(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (WifiDetail detail : data){
            sb.append("Network ");
            sb.append(++i);
            sb.append("\n\n");
            sb.append("SSID : ").append(detail.getSsid()).append("\n");
            sb.append("PSK  : ").append(detail.getPsk()).append("\n\n");
        }
        return sb.toString();
    }

    // Inner class for making the wifi object arraylist in a background thread
    private class WifiDataFetcher extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            // Extracting the wifi supplicant file and getting it as a String
            WifiFileExtractor extractor = new WifiFileExtractor();
            final String fileStr = extractor.returnInString();

            if (fileStr.length() == 0){
                conPSKStr = "No Root Permission";
                isRooted = false;
            }else{
                isRooted = true;
            }

            // Extracting relevant components from wifi supplicant data and making the
            // wifi detail object array list

            if (sdk_int >= Build.VERSION_CODES.O){
                WifiDetailMakerForO makerForO = new WifiDetailMakerForO(fileStr, data);
                makerForO.makeWifiDetailObjects();
            }else {
                WifiDetailMaker maker = new WifiDetailMaker(fileStr,data);
                maker.makeWifiDetailObjects();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            // This method required to add values to the raw data set to enable search
            adapter.setRawDataSet();
            checkForConnectionDetails();
            currentConnectionLayoutInitiator();
            super.onPostExecute(aVoid);
        }
    }

    /*public void popUpWindow(View view){
        //inflating the layout to view
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window,null);

        // creating the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // close when touch outside

        final PopupWindow popupWindow = new PopupWindow(popupView,width,height,focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }*/
}
