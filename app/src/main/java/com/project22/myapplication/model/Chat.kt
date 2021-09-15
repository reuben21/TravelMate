package com.project22.myapplication.model

import java.util.HashMap


class Chat {
    var id: String? = null
    var chatName: String? = null
    var chatImageHolder: String? = null


    constructor() {}

    constructor(id: String, chatName: String, chatImageHolder: String) {
        this.id = id
        this.chatName = chatName
        this.chatImageHolder = chatImageHolder

    }

    constructor( chatName: String, chatImageHolder: String) {
        this.chatName = chatName
        this.chatImageHolder = chatImageHolder

    }



    fun toMap(): Map<String,String> {

        val result = HashMap<String,String>()
        result["chatName"] = chatName!!.toString()
        result["chatImageHolder"] = chatImageHolder!!.toString()

        return result
    }
}