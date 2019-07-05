package com.normorovers.mmt.app.event.mmtevent

interface QRDataReceiver {
	fun handleData(data: String)
}