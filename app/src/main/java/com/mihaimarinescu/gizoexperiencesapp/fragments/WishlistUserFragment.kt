package com.mihaimarinescu.gizoexperiencesapp.fragments

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.R
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingWishlist
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentProfileUserBinding
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentWishlistUserBinding
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking

class WishlistUserFragment : Fragment() {

    private lateinit var binding: FragmentWishlistUserBinding
    private lateinit var imgBookingArrayList: ArrayList<ModelImgBooking>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var adapterImgBooking: AdapterImgBookingWishlist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWishlistUserBinding.inflate(LayoutInflater.from(context), container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        loadWishlistBookings()

        return binding.root
    }

    private fun loadWishlistBookings()
    {
        imgBookingArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Wishlists")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    imgBookingArrayList.clear()

                    for(ds in snapshot.children)
                    {
                        val bookingId = ds.child("bookingId").value.toString()
                        val model = ModelImgBooking()
                        model.id = bookingId

                        imgBookingArrayList.add(model)
                        adapterImgBooking = AdapterImgBookingWishlist(context!!, imgBookingArrayList)
                        binding.wishlistBookingsRv.adapter = adapterImgBooking
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}