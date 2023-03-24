package com.mihaimarinescu.gizoexperiencesapp.models

class ModelImgBooking {
    var uid:String = ""
    var id:String = ""
    var title:String = ""
    var description:String = ""
    var categoryId:String = ""
    var url:String = ""
    var timestamp:Long = 0
    var date:String = ""
    var price:String = ""
    var isInWishlist = false

    constructor()

    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        date: String,
        price: String,
        isInWishlist: Boolean
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.date = date
        this.price = price
        this.isInWishlist = isInWishlist
    }

}