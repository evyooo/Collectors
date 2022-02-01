package com.yoo.collectors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.adapters.AdapterViewBindingAdapter.setOnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.yoo.collectors.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        navigationItemSelect()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.holder_fl_main, fragment)
        fragmentTransaction.commit()
    }

    private fun navigationItemSelect() {
        binding.bnMain.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.action_feed -> replaceFragment(FeedFragment())
                    R.id.action_gallery -> replaceFragment(GalleryFragment())
                    R.id.action_star -> replaceFragment(StarFragment())
                }
                true
            }
            selectedItemId = R.id.action_feed
        }
    }
}