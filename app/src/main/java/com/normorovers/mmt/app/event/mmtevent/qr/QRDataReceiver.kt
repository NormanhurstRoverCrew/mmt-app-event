package com.normorovers.mmt.app.event.mmtevent.qr

interface QRDataReceiver {
	fun handleData(data: String)
}