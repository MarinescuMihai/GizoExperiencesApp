package com.mihaimarinescu.gizoexperiencesapp.activitys

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mihaimarinescu.gizoexperiencesapp.databinding.ActivityImgAddBinding
import com.mihaimarinescu.gizoexperiencesapp.models.ModelCategory

class ImgBookingAddActivity : AppCompatActivity() {
    private lateinit var binding:ActivityImgAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog
    private lateinit var categoryArrayList:ArrayList<ModelCategory>
    private var imgUri : Uri? = null
    private val TAG = "IMG_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.categoryTv.setOnClickListener{
            categoryPickDialog()
        }

        binding.attachImgBtn.setOnClickListener {
            imgPickIntent()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""
    private var date = ""
    private var price:String = ""

    private fun validateData() {
        Log.d(TAG, "validateData")

        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()
        date = binding.dateEt.text.toString().trim()
        price = binding.priceEt.text.toString().trim()

        if(title.isEmpty())
        {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty())
        {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty())
        {
            Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show()
        }
        else if (imgUri == null)
        {
            Toast.makeText(this, "Pick Image", Toast.LENGTH_SHORT).show()
        }
        else if (date.isEmpty())
        {
            Toast.makeText(this, "Enter Date And Time", Toast.LENGTH_SHORT).show()
        }
        else if (price.isEmpty())
        {
            Toast.makeText(this, "Enter price", Toast.LENGTH_SHORT).show()
        }
        else
        {
            uploadImgToStorage()
        }
    }

    private fun uploadImgToStorage() {
        Log.d(TAG,"Upload...")

        progressDialog.setMessage("Uploading...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Bookings/$timestamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(imgUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadImgToStorage: Uploaded, now get the url")

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImgUrl = "${uriTask.result}"

                Log.d(TAG, "uploadImgToStorage: we got the uri: $uploadedImgUrl")

                uploadImgInfoToDb(uploadedImgUrl, timestamp)
            }
            .addOnFailureListener{ e ->
                Log.d(TAG,"uploadImgToStorage: failed to upload due to ${e.message}")
                progressDialog.dismiss()

                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImgInfoToDb(uploadedImgUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadImgInfoToDb: Uploading to DB")
        progressDialog.setMessage("Uploading Image...")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["categoryId"] = selectedCategoryId
        hashMap["url"] = uploadedImgUrl
        hashMap["timestamp"] = 0
        hashMap["viewsCount"] = 0
        hashMap["date"] = date
        hashMap["price"] = price

        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadImgInfoToDb: uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded...", Toast.LENGTH_SHORT).show()
                imgUri = null
            }
            .addOnFailureListener { e->
                Log.d(TAG, "uploadImgInfoToDb: fail to upload to db due to {${e.message}}")
                progressDialog.dismiss()
                Toast.makeText(this, "Fail to upload to db due to {${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadCategories() {
        Log.d(TAG, "loadCategories")
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()

                for(ds in snapshot.children)
                {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private var selectedCategoryTitle = String()
    private var selectedCategoryId = String()

    private fun categoryPickDialog()
    {
        Log.d(TAG,"categoryPickDialog")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for(i in categoryArrayList.indices)
        {
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which ->
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id

                binding.categoryTv.text = selectedCategoryTitle

                Log.d(TAG,"categoryPickDialog: selected category id ${selectedCategoryId}")
                Log.d(TAG,"categoryPickDialog: selected category title ${selectedCategoryTitle}")
            }
            .show()
    }

    private fun imgPickIntent()
    {
        Log.d(TAG,"imgPickIntent")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imgActivityResultLauncher.launch(intent)
    }

    val imgActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == RESULT_OK)
            {
                Log.d(TAG,"IMG Picked ")
                imgUri = result.data!!.data
            }
            else
            {
                Log.d(TAG,"IMG Cancelled ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
























