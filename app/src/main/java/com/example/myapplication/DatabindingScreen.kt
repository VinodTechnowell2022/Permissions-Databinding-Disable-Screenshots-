package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.DatabindingScreenBinding
import com.example.myapplication.databinding.PermissionScreenBinding

//this is how to use databinding in Acitivity class
class DatabindingScreen : AppCompatActivity() {

    lateinit var binding: DatabindingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DatabindingScreenBinding.inflate(layoutInflater) //ctrl+click
        setContentView(binding.root)

        binding.tvHello.text = "Hi This Test Data"
        //very easy

    }


}