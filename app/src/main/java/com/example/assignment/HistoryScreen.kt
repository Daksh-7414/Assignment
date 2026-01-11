package com.example.assignment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryScreen : AppCompatActivity() {

    private lateinit var  database: HistoryDatabase
    private lateinit var  adapter: HistoryAdapter
    private lateinit var historyList :List<HistoryEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_screen)
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

        database = HistoryDatabase.getDatabase(this)

        lifecycleScope.launch {
            historyList = withContext(Dispatchers.IO) {
                database.historyDao().getAllHistory()
            }

            adapter = HistoryAdapter(historyList)
            val recyclerView = findViewById<RecyclerView>(R.id.historyRecycle)
            recyclerView.layoutManager = LinearLayoutManager(this@HistoryScreen)
            recyclerView.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun deleteLinks(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            HistoryDatabase
                .getDatabase(this@HistoryScreen)
                .historyDao()
                .clearHistory()

            withContext(Dispatchers.Main) {
                historyList = emptyList()
                adapter.updateList(emptyList())
            }

        }
    }
    fun uploadLinks(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (historyList.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HistoryScreen, "No history to upload", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val data = historyList.map {
                HistoryModelAPi(
                    url = it.url,
                    timestamp = it.timestamp
                )
            }
            try {
                val api = RetrofitInstance.getApi()
                val response = api.uploadHistory(data)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@HistoryScreen, "History upload", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@HistoryScreen, "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HistoryScreen, "Call Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}