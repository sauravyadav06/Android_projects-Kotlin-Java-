package com.example.loginandsignup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CalculatorActivity : BaseActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calculator)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val number1 = findViewById<EditText>(R.id.number1)
        val number2 = findViewById<EditText>(R.id.number2)
        val add = findViewById<Button>(R.id.add)
        val refresh = findViewById<Button>(R.id.refresh)
        val result = findViewById<TextView>(R.id.result)
        // val go_to_sec_act = findViewById<Button>(R.id.go_to_second_act)

        add.setOnClickListener {
            val num1Text = number1.text.toString()
            val num2Text = number2.text.toString()

            if (num1Text.isEmpty() || num2Text.isEmpty()) {
                result.text = "Please enter both numbers"
                return@setOnClickListener
            }

            try {
                val num1 = num1Text.toBigDecimal()
                val num2 = num2Text.toBigDecimal()
                val sum = num1 + num2
                result.text = sum.toString()

                val formattedResult = "$num1 + $num2 = $sum"

//                val intent = Intent(this, MainActivity2::class.java).apply {
//                    putExtra("SUM_RESULT", formattedResult)
//                }
//                startActivity(intent)

            } catch (e: NumberFormatException) {
                result.text = "Invalid input"
            }
        }

        refresh.setOnClickListener {
            number1.text.clear()
            number2.text.clear()
            result.text = ""
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, SignInActivity::class.java) // Replace MainActivity with your launcher activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Optional: finish the current activity
    }

}