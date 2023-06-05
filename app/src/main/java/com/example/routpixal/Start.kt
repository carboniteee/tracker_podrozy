package com.example.routpixal


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Start : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)

        val btnDodajPodroz = findViewById<Button>(R.id.btnDodaj)
        val btnMapy = findViewById<Button>(R.id.btnMapa)
        val btnLista = findViewById<Button>(R.id.btnLista)

        btnDodajPodroz.setOnClickListener {
            //Dodaj podróż
        }

        btnMapy.setOnClickListener {
            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
        }

        btnLista.setOnClickListener {
            val intent = Intent(this, Lista::class.java)
            startActivity(intent)
        }

    }
}
