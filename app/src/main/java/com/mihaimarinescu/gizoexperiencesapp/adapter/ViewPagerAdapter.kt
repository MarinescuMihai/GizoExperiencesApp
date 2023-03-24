package com.mihaimarinescu.gizoexperiencesapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mihaimarinescu.gizoexperiencesapp.fragments.ChildDashboardUserFragment

class ViewPagerAdapter(fm: FragmentManager, behavior: Int): FragmentPagerAdapter(fm,behavior) {

    private val fragmentsList: ArrayList<ChildDashboardUserFragment> = ArrayList()
    private val fragmentTitleList: ArrayList<String> = ArrayList()

    override fun getCount(): Int {
       return fragmentsList.size
    }

    override fun getItem(position: Int): Fragment {
       return fragmentsList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList[position]
    }

    public fun addFragment(fragment: ChildDashboardUserFragment, title: String)
    {
        fragmentsList.add(fragment)
        fragmentTitleList.add(title)
    }
}