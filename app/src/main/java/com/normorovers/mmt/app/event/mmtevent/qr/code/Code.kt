package com.normorovers.mmt.app.event.mmtevent.qr.code

abstract class Code(type: String) {
	private val primary: String = "MMT19".toUpperCase()
	private val type: String = type.toUpperCase()

	open fun parse(data: String): String {

		val regex = """$primary:$type:""".toRegex()
		val result = regex.findAll(data.toUpperCase()).toList()
		return if (result.isNotEmpty()) {
			val first = result.first()
			val crockfords = """([0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26})""".toRegex()

			val final = crockfords.find(data.toUpperCase(), first.value.length)
			final?.value ?: throw CodeBodyInvalid
		} else {
			throw CodeHeaderWrong
		}

	}
}