package com.example.chromecustomtabforcheckout_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.PayPalAccountNonce
import com.braintreepayments.api.PayPalCheckoutRequest
import com.braintreepayments.api.PayPalClient
import com.braintreepayments.api.PayPalListener
import java.lang.Exception

class MainActivity : AppCompatActivity(), PayPalListener {

    private lateinit var braintreeClient: BraintreeClient
    private lateinit var paypalClient: PayPalClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url = "http://10.0.2.2:3000" // This is the inbuilt 'localhost' address needed.
        // You may need to change the port to your own specified in your own locally hosted server
        val webViewButton: Button = findViewById(R.id.webviewButton)
        val cctButton: Button = findViewById(R.id.checkoutButton)
        val sdkButton: Button = findViewById(R.id.sdkButton)

        try {
            braintreeClient = BraintreeClient(this,
                "sandbox_pgvdg7z2_bypn97dx556968rz",
                "com.example.chromecustomtabforcheckoutdemo")
            paypalClient = PayPalClient(this, braintreeClient)
            paypalClient.setListener(this)
            sdkButton.setOnClickListener {
                Log.d("ButtonClick", "SDK button clicked")
                try {
                    tokenisePayPalAccount()
                    Toast.makeText(this, "Please wait, this takes an age...", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("SDKButtonError", "Error during PayPal tokenization", e)
                    Toast.makeText(this, "Error during PayPal tokenization: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("InitializationError", "Error initializing Braintree or PayPal client", e)
            Toast.makeText(this, "Initialization error: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }
        cctButton.setOnClickListener {
            try {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, android.net.Uri.parse(url))
                Log.d("ButtonClick", "CCT button clicked")
            } catch (e: Exception) {
                Log.e("CCTButtonError", "Error launching Custom Tab", e)
                Toast.makeText(this, "Error launching Custom Tab: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        webViewButton.setOnClickListener {
            if (url.isNotEmpty()) {
                try {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("URL", url)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("WebViewButtonError", "Error launching WebView", e)
                    Toast.makeText(this, "Error launching WebView: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tokenisePayPalAccount() {
        try {
            val request = PayPalCheckoutRequest("100.00")
            request.currencyCode = "GBP"
            paypalClient.tokenizePayPalAccount(this, request)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error tokenizing PayPal account", e)
        }
    }

    override fun onPayPalSuccess(payPalAccountNonce: PayPalAccountNonce) {
        // Handle success
        //Log.d("PayPalSuccess", "PayPal tokenization successful: ${payPalAccountNonce.nonce}"
    }

    override fun onPayPalFailure(error: Exception) {
        // Handle failure
        Log.e("PayPalFailure", "PayPal tokenization failed", error)
        Toast.makeText(this, "PayPal tokenization failed: ${error.message}", Toast.LENGTH_LONG).show()
    }
}