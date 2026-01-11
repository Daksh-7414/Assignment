package com.example.assignment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebViewScreen : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlDisplay: TextView
    private lateinit var currentUrl: String
    private var lastSavedUrl: String? = null
    lateinit var  database: HistoryDatabase

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web_view_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_icon)

        currentUrl = intent.getStringExtra("url") ?: ""
        urlDisplay = findViewById(R.id.urlTitle)
        urlDisplay.text = currentUrl

        database = HistoryDatabase.getDatabase(this)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    currentUrl = url
                    urlDisplay.text = url
                }
                if (url != lastSavedUrl) {
                    lastSavedUrl = url
                    val history = HistoryEntity(
                        url = url ?: "",
                        timestamp = System.currentTimeMillis()
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        database.historyDao().insertHistory(history)
                    }

                }
            }
        }

        if (currentUrl.isNotEmpty()) {
            webView.loadUrl(currentUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_btn -> {
                val intent = Intent()
                intent.putExtra("urlClear", true)
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            android.R.id.home -> {
                val intent = Intent()
                intent.putExtra("urlDisplay", currentUrl)
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            val intent = Intent()
            intent.putExtra("urlDisplay", currentUrl)
            setResult(Activity.RESULT_OK, intent)
            super.onBackPressed()
        }
    }
}