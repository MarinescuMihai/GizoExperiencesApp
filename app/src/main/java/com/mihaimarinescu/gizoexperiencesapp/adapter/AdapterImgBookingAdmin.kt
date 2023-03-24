package com.mihaimarinescu.gizoexperiencesapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.mihaimarinescu.gizoexperiencesapp.*
import com.mihaimarinescu.gizoexperiencesapp.activitys.CheckoutActivity
import com.mihaimarinescu.gizoexperiencesapp.activitys.imgBookingEditActivity
import com.mihaimarinescu.gizoexperiencesapp.databinding.RowImgBookingAdminBinding
import com.mihaimarinescu.gizoexperiencesapp.filters.FilterImgBookingAdmin
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking

class AdapterImgBookingAdmin:RecyclerView.Adapter<AdapterImgBookingAdmin.HolderImgBookingAdmin>, Filterable {

    private var context: Context
    public var imgBookingArrayList: ArrayList<ModelImgBooking>
    private val filterList:ArrayList<ModelImgBooking>

    private lateinit var binding:RowImgBookingAdminBinding

    private var filter: FilterImgBookingAdmin? = null

    constructor(context: Context, imgBookingArrayList: ArrayList<ModelImgBooking>) : super() {
        this.context = context
        this.imgBookingArrayList = imgBookingArrayList
        this.filterList = imgBookingArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImgBookingAdmin {
       binding = RowImgBookingAdminBinding.inflate(LayoutInflater.from(context), parent,false)

        return HolderImgBookingAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImgBookingAdmin, position: Int) {
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

        binding.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        binding.checkoutBtn.setOnClickListener{
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putExtra("imgBookingId",imgBookingId)
            context.startActivity(intent)
        }
    }

    private fun moreOptionsDialog(model: ModelImgBooking, holder: HolderImgBookingAdmin) {
        val bookingId = model.id
        val bookingUrl = model.url
        val bookingTitle = model.title

        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Chose Options")
            .setItems(options){dialog,position ->
                if(position == 0)
                {
                    val intent = Intent(context, imgBookingEditActivity::class.java)
                    intent.putExtra("bookingId", bookingId)
                    context.startActivity(intent)
                }
                else if (position == 1)
                {
                    MyApplication.deleteBooking(context, bookingId, bookingUrl, bookingTitle)
                }
            }
            .show()
    }

    override fun getItemCount(): Int {
        return imgBookingArrayList.size
    }

    override fun getFilter(): Filter {
        if(filter == null)
        {
            filter = FilterImgBookingAdmin(filterList,this)
        }
        return filter as FilterImgBookingAdmin
    }

    inner class  HolderImgBookingAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgView = binding.imgView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val dateTv = binding.dateTv
        val priceTv = binding.priceTv
    }
}