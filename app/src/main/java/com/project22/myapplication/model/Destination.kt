package com.project22.myapplication.model

import com.google.firebase.Timestamp
import java.util.HashMap

//"originName" to originName,
//"destinationName" to destinationName,
//"originLatitude" to originNameLatitude,
//"originLongitude" to originNameLongitude,
//"destinationLatitude" to destinationNameLatitude,
//"destinationLongitude" to destinationNameLongitude,
//"startDate" to Timestamp(Date(startDateVar)) ,
//"endDate" to Timestamp(Date(endDateVar)) ,
//"chatName" to chatName,
//"ticketImageUrl" to ticketImageUrl,
//"destinationImageUrl" to destinationImageUrl

class Destination {
    var id: String? = null
    var creatorId: String? = null
    var creatorName: String? = null
    var chatId: String? = null
    var originName: String? = null
    var destinationName: String? = null
    var chatName: String? = null
    var ticketImageUrl: String? = null
    var destinationImageUrl: String? = null
    var originLatitude: Double? = null
    var originLongitude: Double? = null
    var destinationLatitude: Double? = null
    var destinationLongitude: Double? = null
    var startDate: Timestamp? = null
    var endDate: Timestamp? = null
    var travellers: Int? = null

    constructor() {}


    constructor(
        id: String,
        creatorId: String,
        creatorName: String,
        chatId: String,

        originName: String,
        destinationName: String,
        chatName: String,
        ticketImageUrl: String,
        destinationImageUrl: String,
        originLatitude: Double,
        originLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        startDate: Timestamp,
        endDate: Timestamp?,
        travellers: Int
    ) {
        this.id = id
        this.creatorId = creatorId
        this.creatorName = creatorName
        this.originName = originName
        this.destinationName = destinationName
        this.chatName = chatName
        this.ticketImageUrl = ticketImageUrl
        this.destinationImageUrl = destinationImageUrl
        this.originLatitude = originLatitude
        this.originLongitude = originLongitude
        this.destinationLatitude = destinationLatitude
        this.destinationLongitude = destinationLongitude
        this.startDate = startDate
        this.endDate = endDate
        this.travellers = travellers
        this.chatId = chatId
    }





}