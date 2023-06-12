package com.example.routpixal

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class Lista : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listData: MutableList<String>
    private lateinit var listTemp: MutableList<Podroz?>
    private lateinit var mDataBase: DatabaseReference
    private val USER_KEY = "wiadomosci"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista)
        init()
        getDataFromDB()
        //setOnClickItem()
    }

    private fun init() {
        listView = findViewById(R.id.recyclerView)
        listData = ArrayList()
        listTemp = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY)
    }

    private fun getDataFromDB() {
        val vListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listData.clear()
                listTemp.clear()
                for (ds in dataSnapshot.children) {
                    val podroz: Podroz? = ds.getValue(Podroz::class.java)
                    podroz?.let {
                        it.routeName?.let { name ->
                            listData.add(name)
                        }
                        listTemp.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsłuż błąd odczytu danych z bazy danych
            }
        }
        mDataBase.addValueEventListener(vListener)
    }

    /*private fun setOnClickItem() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val podroz: Podroz? = listTemp[position]
            podroz?.let {
                val intent = Intent(this@Lista, ShowActivity::class.java)
                intent.putExtra(Constant.USER_NAME, it.name)
                intent.putExtra(Constant.USER_SEC_NAME, it.sec_name)
                intent.putExtra(Constant.USER_EMAIL, it.email)
                startActivity(intent)
            }
        }
    }*/
}
