package com.mihaimarinescu.gizoexperiencesapp.models

class ModelCategory {
    var category:String = ""
    var id:String = ""
    var timestamp:Long = 0
    var uid:String = ""
    constructor()
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }
}