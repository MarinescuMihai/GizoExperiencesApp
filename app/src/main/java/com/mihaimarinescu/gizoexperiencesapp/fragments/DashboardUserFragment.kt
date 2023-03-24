package com.mihaimarinescu.gizoexperiencesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.models.ModelCategory
import com.mihaimarinescu.gizoexperiencesapp.adapter.ViewPagerAdapter
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentDashboardUserBinding

class DashboardUserFragment : Fragment() {

    lateinit var binding: FragmentDashboardUserBinding
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentDashboardUserBinding.inflate(LayoutInflater.from(context), container, false)
        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager)
    {
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager,  FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                val modelAll = ModelCategory("01","All",1,"")

                categoryArrayList.add(modelAll)

                viewPagerAdapter.addFragment(
                    ChildDashboardUserFragment()
                        .newInstance(modelAll.id, modelAll.category, modelAll.uid),modelAll.category)

                viewPagerAdapter.notifyDataSetChanged()

                for(ds in snapshot.children)
                {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)

                    viewPagerAdapter.addFragment(
                        ChildDashboardUserFragment()
                            .newInstance(model.id, model.category, model.uid), model.category)

                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        viewPager.adapter = viewPagerAdapter
    }
}