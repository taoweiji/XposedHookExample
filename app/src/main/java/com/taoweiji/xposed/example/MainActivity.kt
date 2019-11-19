package com.taoweiji.xposed.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            val text = getInfo("test", System.currentTimeMillis().toString())
            Log.e("MainActivity",text)
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getInfo(arg1: String, arg2: String): String {
        return "$arg1,$arg2"
    }
}
