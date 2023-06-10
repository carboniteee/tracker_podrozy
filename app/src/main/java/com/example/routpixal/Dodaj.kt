package com.example.routpixal

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class Dodaj : AppCompatActivity() {
    private lateinit var pointsContainer: LinearLayout
    private lateinit var buttonAddPoint: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dodaj)
        pointsContainer = findViewById(R.id.pointsContainer)
        buttonAddPoint = findViewById(R.id.buttonAddPoint)
        buttonAddPoint.setOnClickListener {
            addIntermediatePointField()
        }
    }

    private fun addIntermediatePointField() {
        val pointLayout = layoutInflater.inflate(R.layout.dodatkowy_punkt, pointsContainer, false)
        val buttonDeletePoint = pointLayout.findViewById<Button>(R.id.btnUsun)
        buttonDeletePoint.setOnClickListener {
            pointsContainer.removeView(pointLayout)
        }
        pointsContainer.addView(pointLayout, pointsContainer.childCount - 1)
    }
}
