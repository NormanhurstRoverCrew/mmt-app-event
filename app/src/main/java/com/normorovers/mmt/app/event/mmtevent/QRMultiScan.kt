package com.normorovers.mmt.app.event.mmtevent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class QRMultiScan : AppCompatActivity(), QRDataReceiver {
	private var hasScanned: Boolean = false

	companion object {
		const val REQUEST_CODE = 1002
	}

	override fun handleData(data: String) {
//			supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.fragment)!!).commit();

//			Log.d("QRScanOnce", data)
		val result = Intent()
		result.putExtra("data", data)
		setResult(RESULT_OK, result)
		finish()

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_qrscan_multi)

		supportFragmentManager?.beginTransaction()?.replace(R.id.fragment,
				QRFragment.newInstance(true))?.commit()
	}

}
