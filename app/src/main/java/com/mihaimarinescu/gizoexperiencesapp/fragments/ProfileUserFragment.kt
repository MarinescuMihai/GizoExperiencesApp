package com.mihaimarinescu.gizoexperiencesapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.R
import com.mihaimarinescu.gizoexperiencesapp.activitys.*
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentChildDashboardUserBinding
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentProfileUserBinding

class ProfileUserFragment : Fragment() {

    private lateinit var binding: FragmentProfileUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileUserBinding.inflate(LayoutInflater.from(context), container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.profileEditBtn.setOnClickListener{
            startActivity(Intent(context, ProfileEditActivity::class.java))
        }

        binding.createBookingBtn.setOnClickListener {
            startActivity(Intent(context, ImgBookingAddActivity::class.java))
        }

        return binding.root
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val email = snapshot.child("email").value.toString()
                    val name = snapshot.child("name").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()
                    val uid = snapshot.child("uid").value.toString()

                    binding.nameTv.text = name
                    binding.emailTv.text = email

                    try {
                        Glide.with(this@ProfileUserFragment)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_black)
                            .into(binding.profileIv)
                    }
                    catch (e : Exception)
                    {
                        Toast.makeText(context, "Fail to load avatar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}