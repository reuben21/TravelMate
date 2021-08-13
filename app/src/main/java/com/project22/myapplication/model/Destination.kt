package com.project22.myapplication.model

import java.util.HashMap


class Destination {
    var id: String? = null
    var placeName: String? = null
    var placeImageUrl: String? = null
    var travellers: Int? = null

    constructor() {}

    constructor(id: String, placeName: String, placeImageUrl: String, travellers: Int) {
        this.id = id
        this.placeName = placeName
        this.placeImageUrl = placeImageUrl
        this.travellers = travellers
    }

    constructor( placeName: String, placeImageUrl: String, travellers: Int) {
        this.placeName = placeName
        this.placeImageUrl = placeImageUrl
        this.travellers = travellers
    }



    fun toMap(): Map<String,String> {

        val result = HashMap<String,String>()
        result["placeName"] = placeName!!.toString()
        result["placeImageUrl"] = placeImageUrl!!.toString()
        result["travellers"] = travellers!!.toString()

        return result
    }
}