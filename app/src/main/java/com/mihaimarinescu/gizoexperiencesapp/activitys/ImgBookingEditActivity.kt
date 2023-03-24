package com.mihaimarinescu.gizoexperiencesapp.activitys

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.databinding.ActivityImgBookingEditBinding

class imgBookingEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImgBookingEditBinding

    private var bookingId = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryTitleArrayList: ArrayList<String>
    private lateinit var categoryIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgBookingEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookingId = intent.getStringExtra("bookingId")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadBookingInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun loadBookingInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.child(bookingId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    selectedCategoryId = snapshot.child("categoryId").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val date = snapshot.child("date").value.toString()
                    val price = snapshot.child("price").value.toString()

                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)
                    binding.dateEt.setText(date)
                    binding.priceEt.setText(price)

                    val refBookingCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refBookingCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val category = snapshot.child("category").value
                                binding.categoryTv.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private var title = ""
    private var description = ""
    private var date = ""
    private var price = ""
    private fun validateData()
    {
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        date = binding.dateEt.text.toString().trim()
        price = binding.priceEt.text.toString().trim()

        if(title.isEmpty())
        {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty())
        {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        }
        else if (selectedCategoryId.isEmpty())
        {
            Toast.makeText(this, "Pick Category", Toast.LENGTH_SHORT).show()
        }
        else
        {
            updateBooking()
        }
    }

    private fun updateBooking() {
        progressDialog.setMessage("Updating booking info")
        progressDialog.show()

        val hashMap = HashMap<String,Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["date"] = "$date"
        hashMap["price"] = "$price"

        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.child(bookingId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fail to update due to {${e.message}}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryDialog() {
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoriesArray.indices)
        {
            categoriesArray[i] = categoryTitleArrayList[i]
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chose Category")
            .setItems(categoriesArray){dialog, position ->
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                binding.categoryTv.text = selectedCategoryTitle
            }
            .show()
    }

    private fun loadCategories() {
        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryTitleArrayList.clear()
                categoryIdArrayList.clear()

                for (ds in snapshot.children)
                {
                    val id = ds.child("id").value.toString()
                    val category = ds.child("category").value.toString()

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}