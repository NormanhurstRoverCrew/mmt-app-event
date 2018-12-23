package com.normorovers.mmt.app.event.mmtevent.qreader

class QRAction(_type : String, _data : String) {
    val type : String = _type

    val data : String = _data

    override fun toString(): String {
        return "Type: $type Data: $data"
    }
}