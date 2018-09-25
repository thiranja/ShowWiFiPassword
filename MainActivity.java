package com.example.thiranja.showwifipassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    ArrayList<WifiDetail> data;
    ListView listView;
    private static CustomAdapter adapter;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.list);

        // Extracting the wifi supplicant file and getting it as a String
        WifiFileExtractor extractor = new WifiFileExtractor();
        final String fileStr = extractor.returnInString();

        // Extracting relavant components from wifi supplicant data and making the
        // wifi detail object arraylist
        WifiDetailMaker maker = new WifiDetailMaker(fileStr);
        data= maker.makeWifiDetailObjects();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        //SearchManager searchManager = (SearchManager)
          //      getSystemService(Context.SEARCH_SERVICE);
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
                }else{
                    adapter.filter(s);
                }
                return true;
            }
        });
        return true;
    }

}
