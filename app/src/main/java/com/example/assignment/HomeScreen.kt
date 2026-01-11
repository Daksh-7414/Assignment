package com.example.assignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class HomeScreen : AppCompatActivity() {

    private lateinit var imagePagger: ViewPager2
    private lateinit var bannerDots: WormDotsIndicator
    private lateinit var userUrl: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        imagePagger = findViewById(R.id.onboardingViewPager)
        bannerDots = findViewById(R.id.dotsIndicator)

        val bannerList = listOf(
            R.drawable.image_1,
            R.drawable.image_2,
            R.drawable.image_3
        )
        val imageAdapter = ImageAdapter(bannerList)
        imagePagger.adapter = imageAdapter
        imagePagger.isUserInputEnabled = false
        bannerDots.attachTo(imagePagger)

        userUrl = findViewById(R.id.url)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data?.getBooleanExtra("urlClear", false) == true) {
                userUrl.text.clear()
            } else {
                val urlToDisplay = data?.getStringExtra("urlDisplay")
                urlToDisplay?.let {
                    userUrl.setText(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history_btn -> {
                val intent = Intent(this, HistoryScreen::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openurl(view: View) {
        validateAndOpenUrl()
    }

    private fun validateAndOpenUrl() {

        val input = userUrl.text.toString().trim()

        if (input.isEmpty()) {
            Toast.makeText(this, "Enter URL first", Toast.LENGTH_SHORT).show()
            return
        }

        val finalUrl = if (input.startsWith("https://")) {
            input
        } else {
            "https://$input"
        }

        val urlPattern = "^(https?://)?([\\w-]+\\.)+[a-zA-Z]{2,6}(/\\S*)?$".toRegex()
        if (!urlPattern.matches(finalUrl)) {
            userUrl.error = "Please enter a valid URL"
            userUrl.requestFocus()
            return
        }

        openWebView(finalUrl)
    }

    private fun openWebView(finalUrl: String) {
        val intent = Intent(this, WebViewScreen::class.java)
        intent.putExtra("url", finalUrl)
        startActivityForResult(intent, 1)

    }

}