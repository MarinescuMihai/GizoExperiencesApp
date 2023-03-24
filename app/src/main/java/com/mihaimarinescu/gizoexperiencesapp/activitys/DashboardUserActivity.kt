package com.mihaimarinescu.gizoexperiencesapp.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.mihaimarinescu.gizoexperiencesapp.R
import com.mihaimarinescu.gizoexperiencesapp.databinding.ActivityDashboardUserBinding
import com.mihaimarinescu.gizoexperiencesapp.fragments.BookingsUserFragment
import com.mihaimarinescu.gizoexperiencesapp.fragments.DashboardUserFragment
import com.mihaimarinescu.gizoexperiencesapp.fragments.ProfileUserFragment
import com.mihaimarinescu.gizoexperiencesapp.fragments.WishlistUserFragment

class DashboardUserActivity : AppCompatActivity() {

    private val dashboardFragment = DashboardUserFragment()
    private val wishlistFragment = WishlistUserFragment()
    private val bookingFragment = BookingsUserFragment()
    private val profileUserFragment = ProfileUserFragment()

    private lateinit var binding:ActivityDashboardUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        replaceFragment(dashboardFragment)
        binding.bottomNavigationBnv.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.searchI -> replaceFragment(dashboardFragment)
                R.id.wishlistI -> replaceFragment(wishlistFragment)
                R.id.bookingsI -> replaceFragment(bookingFragment)
                R.id.profileI ->
                    if(firebaseAuth.currentUser != null)
                    {
                        replaceFragment(profileUserFragment)
                    }
                    else
                    {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
            }
            true
        }
    }

    private fun checkUser()
    {
        val firebaseUser = firebaseAuth.currentUser

        if(firebaseUser == null)
        {
            binding.subTitleTv.text = "Not Loggen In"
        }
        else
        {
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }

    private fun replaceFragment(fragment: Fragment)
    {
        if(fragment != null)
        {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerFl,fragment)
            transaction.commit()
        }
    }
}