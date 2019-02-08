package com.myapp.thiranja.showwifipassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HelpActivity extends AppCompatActivity {

    private AdView bottomBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView help = findViewById(R.id.help_tv);
        help.setText(Html.fromHtml(getString(R.string.help_activity_text)));
        help.setMovementMethod(LinkMovementMethod.getInstance());

        // Making the ad view visible in the app
        bottomBanner = findViewById(R.id.help_bottom_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bottomBanner.loadAd(adRequest);
        // Setting the adlistner to remove add space if not available
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
}
