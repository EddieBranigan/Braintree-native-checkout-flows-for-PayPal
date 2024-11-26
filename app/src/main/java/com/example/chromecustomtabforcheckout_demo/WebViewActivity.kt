package com.example.chromecustomtabforcheckout_demo

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = intent.getStringExtra("URL")

        // Check if the URL is not null before loading
        if (url != null) {
            val webView: WebView = findViewById(R.id.webView)
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        } else {
            // Handle the case where the URL is null
            // Show a default page or error message
        }
    }
}
