package com.myapp.thiranja.showwifipassword;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class NoRootModeActivity extends AppCompatActivity {

    private AdView bottomBanner;
    private LinearLayout passwordList;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_root_mode);

        String gateway;

        gateway = getIntent().getStringExtra("GATEWAY");

        // Setting the webview and loading router homepage
        WebView webView = findViewById(R.id.no_root_webview);
        webView.setInitialScale(1);
        webView.getSettings().setMinimumFontSize(20);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setAppCacheMaxSize( 10 * 1024 * 1024 );
        if (gateway.equals("Unknown")) {
            Toast.makeText(this, "An Error has been occurred", Toast.LENGTH_SHORT).show();
        }else{
            webView.loadUrl(gateway);
        }

        TextView expandable = findViewById(R.id.password_expand);
        passwordList = findViewById(R.id.password_list);
        expandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordList.getVisibility() == View.GONE){
                    passwordList.setVisibility(View.VISIBLE);
                }else{
                    passwordList.setVisibility(View.GONE);
                }
            }
        });


        // Making the ad view visible in the app
        bottomBanner = findViewById(R.id.no_root_banner);
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

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3824216403455651/1180902695");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    @Override
    protected void onDestroy() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onDestroy();
    }
}
