package com.yoo.collectors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.yoo.collectors.databinding.FragmentStarBinding


class StarFragment : Fragment() {

    private lateinit var binding: FragmentStarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_star, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}