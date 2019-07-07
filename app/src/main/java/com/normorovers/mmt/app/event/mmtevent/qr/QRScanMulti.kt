package com.normorovers.mmt.app.event.mmtevent.qr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.normorovers.mmt.app.event.mmtevent.R
import kotlinx.android.synthetic.main.activity_qrscan_multi.*

class QRScanMulti : AppCompatActivity(), QRDataReceiver {
	companion object {
		const val REQUEST_CODE = 1002
	}

	private var scanResults: ArrayList<String> = ArrayList()


	override fun handleData(data: String) {
		scanResults.add(data)
	}

	override fun onBackPressed() {
		returnResult()
		super.onBackPressed()
	}

	private fun returnResult() {
		val result = Intent()
		result.putExtra("data", scanResults)
		setResult(RESULT_OK, result)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_qrscan_multi)

		supportFragmentManager?.beginTransaction()?.replace(R.id.fragment,
				QRFragment.newInstance(false))?.commit()

		(button_confirm_selection as FloatingActionButton).setOnClickListener {
			returnResult()
			finish()
		}
	}

}
