package com.normorovers.mmt.app.event.mmtevent.view.ticket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.normorovers.mmt.app.event.mmtevent.R
import kotlinx.android.synthetic.main.activity_ticket_selector.*

class TicketSelectorActivity : AppCompatActivity() {
	companion object {
		const val REQUEST_CODE = 1003
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_ticket_selector)
		setSupportActionBar(toolbar)

		supportFragmentManager.beginTransaction().replace(R.id.fragment, TicketsFragment(null)).commit()
	}

	fun setResult(uid: String) {
		val result = Intent()
		result.putExtra("uid", uid)
		setResult(RESULT_OK, result)
		finish()
	}

}
