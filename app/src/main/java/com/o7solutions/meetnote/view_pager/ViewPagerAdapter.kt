package com.o7solutions.meetnote.view_pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.o7solutions.meetnote.FirstFragment
import com.o7solutions.meetnote.SecondFragment
import com.o7solutions.meetnote.ThirdFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        FirstFragment(),
        SecondFragment(),
        ThirdFragment()

    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
