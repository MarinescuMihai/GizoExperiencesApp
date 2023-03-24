package com.mihaimarinescu.gizoexperiencesapp.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingAdmin
import com.mihaimarinescu.gizoexperiencesapp.databinding.ActivityImgBookingListAdminBinding
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking
import java.lang.Exception

class ImgBookingListAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImgBookingListAdminBinding
    private lateinit var firebaseAuth : FirebaseAuth

    private var categoryId = ""
    private var category = ""

    private lateinit var imgBookingArrayList:ArrayList<ModelImgBooking>
    private lateinit var adapterImgBookingAdmin: AdapterImgBookingAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgBookingListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        binding.subTitleTv.text = category

        loadImgBookingList()

        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterImgBookingAdmin.filter.filter(s)
                }
                catch (e :Exception)
                {

                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadImgBookingList() {
        imgBookingArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object :ValueEventListener
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

                    adapterImgBookingAdmin = AdapterImgBookingAdmin(this@ImgBookingListAdminActivity, imgBookingArrayList)
                    binding.bookingsRv.adapter = adapterImgBookingAdmin
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}