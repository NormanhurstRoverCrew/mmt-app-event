package com.normorovers.mmt.app.event.mmtevent.db

class User(val name: String, val mobile: String) {

    override fun toString(): String {
        return "name: $name, mobile: $mobile"
    }
}