package com.project22.myapplication.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}