package com.example.musicapimvvm.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapimvvm.R
import com.example.musicapimvvm.ui.activity.SearchActivity
import com.example.musicapimvvm.ui.adapter.ViewPagerAdapter
import com.example.mydiary.util.CustomBackStackFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_rank.*


class RankFragment : CustomBackStackFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewpager()

        startSearchActivity()
    }

    private fun setViewpager() {
        var listFragment: MutableList<Fragment> = mutableListOf(
            MusicVietnameseFragment(),
            MusicUSUKFragment()
        )

        val adapterViewPagers = ViewPagerAdapter(
            listFragment,
            childFragmentManager,
            lifecycle
        )

        view_pager_top_100.adapter = adapterViewPagers

        setTabLayout()
    }

    private fun setTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Vietnam"))
        tabLayout.addTab(tabLayout.newTab().setText("US - UK"))
        tabLayout.setTabTextColors(
            ContextCompat.getColor(requireContext(), R.color.black),
            ContextCompat.getColor(requireContext(), R.color.amethyst)
        )
        tabLayout.setSelectedTabIndicator(0)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view_pager_top_100.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        view_pager_top_100.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun startSearchActivity() {
        btnSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

}