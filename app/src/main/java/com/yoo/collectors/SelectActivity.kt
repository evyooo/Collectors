package com.yoo.collectors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yoo.collectors.databinding.ActivitySelectBinding

class SelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}