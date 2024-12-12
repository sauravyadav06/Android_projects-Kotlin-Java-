package com.example.addition

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val number1 = findViewById<EditText>(R.id.result1)
        val number2 = findViewById<EditText>(R.id.number2)
        val add = findViewById<Button>(R.id.add)
        val result = findViewById<TextView>(R.id.)

        add.setOnClickListener{
            var num1 = number1.text.toString().toInt()
            var num2 = number2.text.toString().toInt()

            var sum = num1+num2
            result.text = sum.toString()
        }
    }
}