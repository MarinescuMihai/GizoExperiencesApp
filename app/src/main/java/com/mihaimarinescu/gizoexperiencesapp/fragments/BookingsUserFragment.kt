package com.mihaimarinescu.gizoexperiencesapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingAdmin
import com.mihaimarinescu.gizoexperiencesapp.databinding.ActivityImgBookingListAdminBinding
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentBookingsUserBinding
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking
import java.lang.Exception

class BookingsUserFragment : Fragment() {

    private lateinit var binding: FragmentBookingsUserBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private var categoryId = ""
    private var category = ""

    private lateinit var imgBookingArrayList:ArrayList<ModelImgBooking>
    private lateinit var adapterImgBookingAdmin: AdapterImgBookingAdmin

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBookingsUserBinding.inflate(LayoutInflater.from(context), container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        loadImgBookingList()

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterImgBookingAdmin.filter.filter(s)
                }
                catch (e : Exception)
                {

                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        return binding.root
    }

    private fun loadImgBookingList() {
        imgBookingArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.orderByChild("uid").equalTo(firebaseAuth.uid)
            .addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot) {
                    imgBookingArrayList.clear()
                    for(ds in snapshot.children)
                    {
                        val model = ds.getValue(ModelImgBooking::class.java)
                        if(model != null)
                        {
                            imgBookingArrayList.add(model)
                        }
                    }

                    adapterImgBookingAdmin = AdapterImgBookingAdmin(context!!, imgBookingArrayList)
                    binding.bookingsRv.adapter = adapterImgBookingAdmin
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}