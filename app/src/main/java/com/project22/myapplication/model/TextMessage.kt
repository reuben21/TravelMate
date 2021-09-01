package com.project22.myapplication.model

import com.google.firebase.Timestamp
import java.util.HashMap


class TextMessage  {

     var id: String?  = null
     var message: String?  = null
     var createdAt: Timestamp?  = null
     var senderId: String?  = null
     var senderName: String?  = null
     var messageType: String? = null

    constructor()

    constructor(
        id: String?,
        message: String?,
        createdAt: Timestamp,
        senderId: String,
        senderName: String,
        messageType: String
    ) {
        this.id = id
        this.message = message
        this.createdAt = createdAt
        this.senderId = senderId
        this.senderName = senderName
        this.messageType = messageType
    }

//    fun toMap(): Map<Any,Any> {
//
//        val result = HashMap<Any,Any>()
//        result["id"] = id
//        result["placeImageUrl"] = message!!.toString()
//        result["travellers"] = createdAt
//        result["placeImageUrl"] = senderId!!.toString()
//        result["travellers"] = senderName!!.toString()
//        return result
//    }

}