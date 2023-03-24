package com.mihaimarinescu.gizoexperiencesapp.filters

import android.widget.Filter
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterImgBookingAdmin
import com.mihaimarinescu.gizoexperiencesapp.models.ModelImgBooking

class FilterImgBookingAdmin :Filter{
    var filterList:ArrayList<ModelImgBooking>
    var adapterImgBookingAdmin: AdapterImgBookingAdmin

    constructor(filterList: ArrayList<ModelImgBooking>, adapterImgBookingAdmin: AdapterImgBookingAdmin)
    {
        this.filterList = filterList
        this.adapterImgBookingAdmin = adapterImgBookingAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint
        val results = FilterResults()

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

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterImgBookingAdmin.imgBookingArrayList = results!!.values as ArrayList<ModelImgBooking>

        adapterImgBookingAdmin.notifyDataSetChanged()
    }
}