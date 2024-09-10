package com.example.myapplication

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var creditScoreValueTextView: TextView
    private lateinit var creditScoreProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        creditScoreValueTextView = findViewById(R.id.creditScoreValue)
        creditScoreProgressBar = findViewById(R.id.creditScoreProgressBar)

        // Sample credit score
        val creditScore = 75 // Example value
        updateCreditScore(creditScore)
    }

    private fun updateCreditScore(score: Int) {
        // Update the TextView with the credit score
        creditScoreValueTextView.text = "$score"

        // Update the ProgressBar with the credit score
        creditScoreProgressBar.progress = score
    }
}
