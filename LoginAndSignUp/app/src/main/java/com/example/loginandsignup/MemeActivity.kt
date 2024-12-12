package com.example.loginandsignup

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        // Handling edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val textView1 = findViewById<TextView>(R.id.textView3)
        val textView2 = findViewById<TextView>(R.id.textView4)
        val textView3 = findViewById<TextView>(R.id.textView5)
        val buttonFetch = findViewById<Button>(R.id.button2)

        // Set button click listener
        buttonFetch.setOnClickListener {
            getData(textView1, textView2, textView3)
        }
    }

    // Method to fetch data from API and display it in TextViews
    private fun getData(textView1: TextView, textView2: TextView, textView3: TextView) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.restful-api.dev/objects/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseBody> = apiService.getData()

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val json = response.body()!!.string()
                        val jsonObject = JSONObject(json)

                        // Check if JSON has specific fields and set them to TextViews
                        if (jsonObject.has("id")) {
                            val id = jsonObject.getString("id")
                            textView1.text = "ID: $id"
                        }

                        if (jsonObject.has("name")) {
                            val name = jsonObject.getString("name")
                            textView2.text = "Name: $name"
                        }

                        if (jsonObject.has("data")) {
                            val data = jsonObject.getString("data")
                            val dataObject = JSONObject(data)
                            val year = dataObject.getString("year")
                            val price = dataObject.getString("price")
                            val cpu = dataObject.getString("CPU model")

                            textView3.text = "Year: $year, Price: $price, CPU: $cpu"
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@MemeActivity, "Parsing error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MemeActivity, "Response error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@MemeActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
