package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.databinding.DatabindingScreenBinding
import com.example.myapplication.databinding.FragmentOneBinding


//ths is how in fragment we use data binding
class OneFragment : Fragment() {

    lateinit var mBinding: FragmentOneBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentOneBinding.inflate(layoutInflater)

        mBinding.tvHello.text = "Hi This Test Data"

        //Done
        return mBinding.root
    }

}