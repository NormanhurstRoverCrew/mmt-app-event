package com.normorovers.mmt.app.event.mmtevent.db

class Ticket(val uid: String?, val user: User) {

    override fun toString(): String {
        return "uid: $uid, user: {$user}"
    }
}