package com.example.routpixal

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity

class Dodaj : AppCompatActivity() {
    private lateinit var pointsContainer: LinearLayout
    private lateinit var btnAddPoint: Button
    private lateinit var edRouteName: EditText
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dodaj)
        pointsContainer = findViewById(R.id.pointsContainer)
        btnAddPoint = findViewById(R.id.btnAddPoint)
        edRouteName = findViewById(R.id.edRouteName)
        ratingBar = findViewById(R.id.ratingBar)

        btnAddPoint.setOnClickListener {
            addIntermediatePointField()
        }

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val routeName = edRouteName.text.toString()
            val rating = ratingBar.rating

            val allPoints = mutableListOf<String>()

            val startPoint = findViewById<EditText>(R.id.edStart).text.toString()
            allPoints.add(startPoint)

            for (i in 0 until pointsContainer.childCount - 1) {
                val pointLayout = pointsContainer.getChildAt(i) as LinearLayout
                val editTextPoint = pointLayout.findViewById<EditText>(R.id.pointsContainer)
                val point = editTextPoint.text.toString()
                allPoints.add(point)
            }

            val endPoint = findViewById<EditText>(R.id.edEnd).text.toString()
            allPoints.add(endPoint)

            // teraz te dane można wpisać do bazy danych
        }
    }

    private fun addIntermediatePointField() {
        val pointLayout = LayoutInflater.from(this).inflate(R.layout.dodatkowy_punkt, pointsContainer, false)
        val buttonDeletePoint = pointLayout.findViewById<Button>(R.id.btnUsun)
        buttonDeletePoint.setOnClickListener {
            pointsContainer.removeView(pointLayout)
        }
        pointsContainer.addView(pointLayout, pointsContainer.childCount - 1)
    }
}
