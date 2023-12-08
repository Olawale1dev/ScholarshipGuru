package tso.scholarshipGuru;

import static android.content.ContentValues.TAG;
import static android.webkit.WebView.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.RequestConfiguration.*;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;



import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.widget.ProgressBar;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.Toolbar;
import java.util.Arrays;
import java.util.Calendar;
import android.view.KeyEvent;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.LoadAdError;
public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private SharedPreferences mPrefs;
    private static final String TAG = "MainActivity";
    private WebView webView;
    private WebViewClient WebViewClient;
    private Toast Toast;
    private ImageButton resetButton;
    private ProgressBar progressBar;
    private Handler adHandler = new Handler(Looper.getMainLooper());
    private static final long AD_DISPLAY_INTERVAL = 10 * 60 * 1000; // 10 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.xml_webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://scholarshipguru.com.ng");

        showInterstitialAd();

        //new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("356D0A1542A015D4A7F240498FA05A10"));

        if (webView != null) {
            // Access WebView settings
            //WebSettings webSettings = webView.getSettings();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportMultipleWindows(true);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new MyWebViewClient());
            webView.setWebChromeClient(new WebChromeClient());


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Hide ProgressBar when the page is loaded
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            });

            // Other WebView setup...
        } else {
            // Handle the case where WebView is null
            Toast.makeText(this, "WebView is not available", Toast.LENGTH_SHORT).show();
            // Or, you can take alternative actions, such as finishing the activity.
            finish();
        }


        //Initialize refresh Button
        resetButton = findViewById(R.id.resetButton);

        //Method that calls the refresh button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Refresh WebView content
                webView.reload();
                //showInterstitialAd();
            }
        });




        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


// Initialize the interstitial ad
        InterstitialAd.load(this, "ca-app-pub-9163926251753325/3785290621", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        // Load the ad
        loadInterstitialAd();

        // Schedule the display of the ad every 10 minutes
        scheduleAdDisplay();

        showInterstitialAd();

        // mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.load(this, "ca-app-pub-9163926251753325/3785290621", adRequest,
                new AdLoadCallback());

    }



    private void scheduleAdDisplay() {
        adHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Display the interstitial ad
                showInterstitialAd();

                // Schedule the next ad display
                scheduleAdDisplay();


            }
        }, AD_DISPLAY_INTERVAL);


    }

    private final class AdLoadCallback extends InterstitialAdLoadCallback {
        @Override
        public void onAdLoaded(@NonNull InterstitialAd ad) {
            // Ad loaded successfully
            mInterstitialAd = ad;

            // Set FullScreenContentCallback only if the ad is loaded successfully
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed
                    // Load the next ad after it's dismissed
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show
                    // Load the next ad after it fails to show
                    loadInterstitialAd();
                }
            });
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            // Ad failed to load
            // Load the next ad after it fails to load
            loadInterstitialAd();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Check if the key event was the Forward button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_FORWARD) && webView.canGoForward()) {
            webView.goForward();
            return true;
        }

        // If it wasn't the Forward key or there's no web page history, bubble up to the default
        // system behavior
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("WebViewActivity", "onBackPressed called");
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // If there's no history, exit the app (you can customize this behavior)
            super.onBackPressed();
        }
    }


   private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
        Log.d(TAG, "The interstitial ad wasn't loaded yet.");
    }
}


//RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("356D0A1542A015D4A7F240498FA05A10"));



}










