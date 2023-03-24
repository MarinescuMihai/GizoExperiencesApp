package com.mihaimarinescu.gizoexperiencesapp.filters

import android.widget.Filter
import com.mihaimarinescu.gizoexperiencesapp.adapter.AdapterCategory
import com.mihaimarinescu.gizoexperiencesapp.models.ModelCategory

class FilterCategory: Filter {

    private var filterList : ArrayList<ModelCategory>

    private var adapterCategory: AdapterCategory

    constructor(filerList: ArrayList<ModelCategory>, adaperCategory: AdapterCategory) : super() {
        this.filterList = filerList
        this.adapterCategory = adaperCategory
    }


    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        if(constraint != null && constraint.isNotEmpty())
        {
            constraint = constraint.toString().uppercase()
            val filterModels:ArrayList<ModelCategory> = ArrayList()

            for(i in 0 until  filterModels.size)
            {
                if(filterList[i].category.uppercase().contains(constraint)){
                    filterModels.add(filterList[i])
                }
            }
            results.count = filterModels.size
            results.values = filterModels
        }
        else
        {
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>

        adapterCategory.notifyDataSetChanged()
    }
}