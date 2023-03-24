package com.mihaimarinescu.gizoexperiencesapp.filters

import android.widget.Filter
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingUser
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking

class FilterImgBookingUser: Filter{

    var filterList:ArrayList<ModelImgBooking>
    var adapterImgBookingUser: AdapterImgBookingUser

    constructor(filterList: ArrayList<ModelImgBooking>, adapterImgBookingUser: AdapterImgBookingUser)
    {
        this.filterList = filterList
        this.adapterImgBookingUser = adapterImgBookingUser
    }

    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        var constraint:CharSequence? = constraint
        val results = Filter.FilterResults()

        if(constraint != null && constraint.isNotEmpty())
        {
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<ModelImgBooking>()
            for(i in filterList.indices)
            {
                if(filterList[i].title.lowercase().contains(constraint))
                {
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else
        {
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
        adapterImgBookingUser.imgBookingArrayList = results!!.values as ArrayList<ModelImgBooking>

        adapterImgBookingUser.notifyDataSetChanged()
    }
}