package tso.scholarshipGuru;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

// Custom WebViewClient to handle click events
public class MyWebViewClient extends WebViewClient {

    private WebView webView;
    private ProgressBar progressBar;



    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        // Use the application context to avoid the "not an enclosing class" error
        /*Toast.makeText(view.getContext(), "Link clicked!", Toast.LENGTH_SHORT).show();
               return true;*/
        Uri uri = request.getUrl();
        String url = uri.toString();

        // Check if the URL is related to YouTube
        if (url.startsWith("https://www.youtube.com/")) {
            openYouTubeLink(view, url);
            return true;
        }

        // Check if the URL is a Play Store link
        if (url.startsWith("https://play.google.com/")) {
            openPlayStoreLink(view, url);
            return true;
        }

        // For other URLs, let the WebView handle the navigation
        return false;
    }

    private void openYouTubeLink(WebView view, String url) {
        // Open YouTube link in external browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
    }

    private void openPlayStoreLink(WebView view, String url) {
        // Open Play Store link in external browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
    }






    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // Show ProgressBar when page starts loading
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // Hide ProgressBar when page finishes loading
        progressBar.setVisibility(ProgressBar.GONE);

    }


}


