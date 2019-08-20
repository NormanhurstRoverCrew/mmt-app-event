package com.normorovers.mmt.app.event.mmtevent.view.ticket

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.TicketRepository
import com.normorovers.mmt.app.event.mmtevent.qr.QRScanOnce
import com.normorovers.mmt.app.event.mmtevent.qr.code.CodeBodyInvalid
import com.normorovers.mmt.app.event.mmtevent.qr.code.CodeHeaderWrong
import com.normorovers.mmt.app.event.mmtevent.qr.code.TicketCode
import kotlinx.android.synthetic.main.activity_ticket.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.Future

class TicketActivity : AppCompatActivity() {
	private var uid: String? = ""
	private lateinit var paid: Future<PaymentResult>

	companion object {
		const val REQUEST_CODE = 2001
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_ticket)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		uid = intent.getStringExtra("uid")

		if (uid.isNullOrEmpty()) {
			startActivityForResult(Intent(this, QRScanOnce::class.java), REQUEST_CODE)
		} else {
			initWithUid(uid!!)
		}
	}

	override fun onStart() {
		super.onStart()
		if (!uid.isNullOrEmpty()) {
			doAsync { afterInitWaitForPaymentStatus() }
		}
	}

	private fun initWithUid(uid: String) {
		paid = TicketRepository(application).getPaid(uid, {})

		val ticket = TicketRepository(application).getByUidObservable(uid)

		ticket.observe(this, Observer {
			(name as TextView).text = it.user.name
			(mobile as TextView).text = it.user.mobile
			(crew as TextView).text = it.user.crew
		})


	}

	private fun afterInitWaitForPaymentStatus() {

		val ps = (payment_status as TextView)
		val paymentResult = paid.get() ?: return
		runOnUiThread {
			if (paymentResult.paid) {
				ps.text = getString(R.string.payment_paid)
				ps.setTextColor(Color.GREEN)
			} else {
				ps.text = getString(R.string.payment_unpaid, paymentResult.due)
				ps.setTextColor(Color.RED)
				MediaPlayer.create(application, R.raw.ding_error).start()
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_CODE -> { //For opening a team
				when (resultCode) {
					RESULT_OK -> {
						val scannedData = data?.getStringExtra("data")!!
						try {
							val ticketUid = TicketCode().parse(scannedData)

							Log.d("Ticket", ticketUid)

							val i = Intent(application, TicketActivity::class.java)
							i.putExtra("uid", ticketUid)
							startActivity(i)
							finish()
						} catch (e: CodeHeaderWrong) {

						} catch (e: CodeBodyInvalid) {

						}
					}
					RESULT_CANCELED -> {
						finish()
					}
				}
			}
		}
	}

}
