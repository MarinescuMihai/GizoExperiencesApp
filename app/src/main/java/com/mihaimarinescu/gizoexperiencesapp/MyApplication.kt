package com.mihaimarinescu.gizoexperiencesapp

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingWishlist
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking
import java.io.File

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{

        fun loadImgBookingFromUrlSinglePage(
            imgUrl:String,
            imgBookingTitle:String,
            imgBookingView: ShapeableImageView,
            progressBar:ProgressBar)
        {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl)

            val localFile = File.createTempFile(System.currentTimeMillis().toString(),"jpg")
            ref.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    imgBookingView.setImageBitmap(bitmap)
                    progressBar.visibility = View.INVISIBLE
            }
                .addOnFailureListener() { e->
                    progressBar.visibility = View.INVISIBLE
                    Log.d("SET_IMG_TAG", "loadImgBookingFromUrlSinglePage: {${e.message}}")
            }
        }

        fun loadCategory(categoryId:String, categoryTv: TextView)
        {
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category = "${snapshot.child("category").value}"
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun deleteBooking(context:Context, bookingId:String, bookingUrl:String, bookingTitle:String)
        {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait...")
            progressDialog.setMessage("Deleting booking...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookingUrl)
            storageReference.delete()
                .addOnSuccessListener {
                    val ref = FirebaseDatabase.getInstance().getReference("Bookings")
                    ref.child(bookingId)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(context, "Fail to delete from DB due to ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(context, "Fail to delete from storage due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        public fun removeFromWishlist(context: Context, imgBookingId: String)
        {
            val firebaseAuth = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Wishlists").child(imgBookingId)
                .removeValue()
                .addOnSuccessListener {

                }
                .addOnFailureListener { e->
                    Toast.makeText(context, "Failed to remove from Wishlist: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}