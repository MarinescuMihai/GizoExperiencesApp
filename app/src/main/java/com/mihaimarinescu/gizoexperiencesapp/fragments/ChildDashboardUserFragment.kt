package com.mihaimarinescu.gizoexperiencesapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingUser
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking
import com.mihaimarinescu.gizoexperiencesapp.databinding.FragmentChildDashboardUserBinding

class ChildDashboardUserFragment : Fragment {

    lateinit var binding: FragmentChildDashboardUserBinding

    public fun newInstance(categoryId:String, category: String, uit: String): ChildDashboardUserFragment
    {
        val fragment = ChildDashboardUserFragment()
        val args = Bundle()
        args.putString("categoryId", categoryId)
        args.putString("category", category)
        args.putString("uid", uit)
        fragment.arguments = args

        return fragment
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var imgBookingArrayList: ArrayList<ModelImgBooking>
    private lateinit var adapterImgBookingUser: AdapterImgBookingUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments

        if(args != null)
        {
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentChildDashboardUserBinding.inflate(LayoutInflater.from(context), container, false)

        if (category == "All")
        {
            loadAllBookings()
        }
        else
        {
            loadCategorizedBookings()
        }

        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterImgBookingUser.filter.filter(s)
                }
                catch (e: Exception)
                {
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        return binding.root
    }

    private fun loadAllBookings() {
        imgBookingArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imgBookingArrayList.clear()

                for(ds in snapshot.children)
                {
                    val model = ds.getValue(ModelImgBooking::class.java)
                    imgBookingArrayList.add(model!!)
                }

                adapterImgBookingUser = AdapterImgBookingUser(context!!, imgBookingArrayList)
                binding.bookingsRv.adapter = adapterImgBookingUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadCategorizedBookings() {
        imgBookingArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    imgBookingArrayList.clear()

                    for(ds in snapshot.children)
                    {
                        val model = ds.getValue(ModelImgBooking::class.java)
                        imgBookingArrayList.add(model!!)
                    }

                    adapterImgBookingUser = AdapterImgBookingUser(context!!, imgBookingArrayList)
                    binding.bookingsRv.adapter = adapterImgBookingUser
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}