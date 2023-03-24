package com.mihaimarinescu.gizoexperiencesapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mihaimarinescu.gizoexperiencesapp.*
import com.mihaimarinescu.gizoexperiencesapp.activitys.CheckoutActivity
import com.mihaimarinescu.gizoexperiencesapp.databinding.RowImgBookingUserBinding
import com.mihaimarinescu.gizoexperiencesapp.filters.FilterImgBookingUser
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking

class AdapterImgBookingUser: RecyclerView.Adapter<AdapterImgBookingUser.HolderImgBookingUser>, Filterable{

    private var context: Context
    public var imgBookingArrayList: ArrayList<ModelImgBooking>
    private val filterList:ArrayList<ModelImgBooking>
    private var isInWishlist = false

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: RowImgBookingUserBinding

    private var filter: FilterImgBookingUser? = null

    constructor(context: Context, imgBookingArrayList: ArrayList<ModelImgBooking>) : super() {
        this.context = context
        this.imgBookingArrayList = imgBookingArrayList
        this.filterList = imgBookingArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImgBookingUser {
        binding = RowImgBookingUserBinding.inflate(LayoutInflater.from(context), parent,false)

        return HolderImgBookingUser(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImgBookingUser, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()

        val model = imgBookingArrayList[position]
        val imgBookingId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val descriptor = model.description
        val imgBookingUrl = model.url
        val timestamp = model.timestamp
        val date = model.date
        val price = model.price

        holder.titleTv.text = title
        holder.dateTv.text = date
        holder.priceTv.text = "From $$price /person"

        MyApplication.loadImgBookingFromUrlSinglePage(
            imgBookingUrl,
            title,
            holder.imgView,
            holder.progressBar
        )

        holder.checkoutBtn.setOnClickListener{
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putExtra("imgBookingId",imgBookingId)
            context.startActivity(intent)
        }

        if(firebaseAuth.currentUser != null)
        {
            checkIsWishlist(imgBookingId,holder)
        }

        holder.addToWishlistBtn.setOnClickListener{
            if(firebaseAuth.currentUser == null)
            {
                Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show()
            }
            else
            {
                checkIsWishlist(imgBookingId, holder)

                if(isInWishlist)
                {
                    MyApplication.removeFromWishlist(context,imgBookingId)
                }
                else
                {
                    addToWishlist(imgBookingId)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imgBookingArrayList.size
    }

    override fun getFilter(): Filter {
        if(filter == null)
        {
            filter = FilterImgBookingUser(filterList,this)
        }
        return filter as FilterImgBookingUser
    }


    private fun addToWishlist(imgBookingId: String)
    {
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any?>()
        hashMap["bookingId"] = imgBookingId
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Wishlists").child(imgBookingId)
            .setValue(hashMap)
            .addOnSuccessListener {

            }
            .addOnFailureListener {e->
                Toast.makeText(context, "Fail to add to Wishlist: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkIsWishlist(imgBookingId: String, holder: HolderImgBookingUser)
    {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Wishlists").child(imgBookingId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInWishlist = snapshot.exists()

                    if(snapshot.exists())
                    {
                        holder.addToWishlistBtn.setImageResource(R.drawable.ic_favorite_filled_white)
                    }
                    else
                    {
                        holder.addToWishlistBtn.setImageResource(R.drawable.ic_favorite_white)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    inner class  HolderImgBookingUser(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgView = binding.imgView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val dateTv = binding.dateTv
        val priceTv = binding.priceTv
        val addToWishlistBtn = binding.addToWishlistBtn
        val checkoutBtn = binding.checkoutBtn
    }
}