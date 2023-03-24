package com.mihaimarinescu.gizoexperiencesapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.MyApplication
import com.mihaimarinescu.gizoexperiencesapp.R
import com.mihaimarinescu.gizoexperiencesapp.activitys.CheckoutActivity
import com.mihaimarinescu.gizoexperiencesapp.databinding.RowImgBookingUserBinding
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking

class AdapterImgBookingWishlist: RecyclerView.Adapter<AdapterImgBookingWishlist.HolderImgBookingWishlist> {

    private var context: Context
    public var imgBookingArrayList: ArrayList<ModelImgBooking>
    private lateinit var binding: RowImgBookingUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    constructor(context: Context, imgBookingArrayList: ArrayList<ModelImgBooking>) {
        this.context = context
        this.imgBookingArrayList = imgBookingArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImgBookingWishlist {
        binding = RowImgBookingUserBinding.inflate(LayoutInflater.from(context), parent,false)

        return HolderImgBookingWishlist(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImgBookingWishlist, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()
        val model = imgBookingArrayList[position]

        loadImgBookingDetails(model, holder)

        holder.checkoutBtn.setOnClickListener{
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putExtra("imgBookingId",model.id)
            context.startActivity(intent)
        }

        holder.addToWishlistBtn.setOnClickListener{
            MyApplication.removeFromWishlist(context, model.id)
            imgBookingArrayList.removeAt(position)
            this.notifyItemRemoved(position)
        }
    }

    private fun loadImgBookingDetails(model: ModelImgBooking, holder: AdapterImgBookingWishlist.HolderImgBookingWishlist) {
        val bookingId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Bookings")
        ref.child(bookingId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = snapshot.child("categoryId").value.toString()
                    val date = snapshot.child("date").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val id = snapshot.child("id").value.toString()
                    val price = snapshot.child("price").value.toString()
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val title = snapshot.child("title").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val url = snapshot.child("url").value.toString()

                    model.isInWishlist = true
                    model.categoryId = categoryId
                    model.date = date
                    model.description = description
                    model.id = id
                    model.price = price
                    model.timestamp = timestamp.toLong()
                    model.title = title
                    model.uid = uid
                    model.url = url

                    MyApplication.loadImgBookingFromUrlSinglePage(url,title, holder.imgView, holder.progressBar)
                    holder.addToWishlistBtn.setImageResource(R.drawable.ic_remove_purple)

                    holder.titleTv.text = title
                    holder.dateTv.text = date
                    holder.priceTv.text = "From $$price /person"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun getItemCount(): Int {
        return imgBookingArrayList.size
    }

    inner class  HolderImgBookingWishlist(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgView = binding.imgView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val dateTv = binding.dateTv
        val priceTv = binding.priceTv
        val addToWishlistBtn = binding.addToWishlistBtn
        val checkoutBtn = binding.checkoutBtn
    }
}