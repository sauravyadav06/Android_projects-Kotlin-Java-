package com.example.loginandsignup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.loginandsignup.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnCalc.setOnClickListener{
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }
        binding.btnCamera.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.btnfetch.setOnClickListener{
            val intent = Intent(this, MemeActivity::class.java)
            startActivity(intent)
        }

//        binding.btnCamera.setOnClickListener{
//            val intent = Intent(this, SignInActivity::class.java)
//        }
//
//        binding.btnfetch.setOnClickListener{
//            val intent = Intent(this, DashboardActivity::class.java)
//        }
//
//        binding.btnfetch.setOnClickListener{
//            val intent= Intent(this, BaseActivity::class.java)
//        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, SignInActivity::class.java) // Replace MainActivity with your launcher activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Optional: finish the current activity
    }
}