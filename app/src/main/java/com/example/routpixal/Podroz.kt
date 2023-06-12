package com.example.routpixal

class Podroz {

    var id: String? = null
    var routeName: String? = null
    var rating: Float? = null
    var allPoints: MutableList<String>? = null

    constructor() {}
    constructor(id: String?, routeName: String?, rating: Float, allPoints: MutableList<String>) {
        this.id = id
        this.routeName = routeName
        this.rating = rating
        this.allPoints = allPoints
    }
}
