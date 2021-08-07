package com.project22.myapplication.model

import java.util.*

data class Destination( val placeName: String,
//                        val timeOfPost: Date,
//                        val travelingDate: String,
//                        val creator: String,
                        val travellers: Int,
                        val placeImageUrl:String
                        )
     {
    constructor() : this("", 0, "")
}
