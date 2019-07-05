package com.normorovers.mmt.app.event.mmtevent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.fragment_qr.*


private const val SCAN_ONCE = "param1"
private const val SCAN_DELAY = 1500 // ms till re scan

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QRFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [QRFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QRFragment : Fragment() {
	private var scan_once: Boolean = true

	private var lastScanTime: Long = 0
	private var lastScanValue: String = ""
	private var hasScanned: Boolean = false

	private lateinit var qrEader: QREader

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			scan_once = it.getBoolean(SCAN_ONCE, true)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_qr, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		// Init QREader
		// ------------
		qrEader = QREader.Builder(context, camera_view, QRDataListener { data ->
			handleData(data)
		}).facing(QREader.BACK_CAM)
				.enableAutofocus(true)
				.height(camera_view.height)
				.width(camera_view.width)
				.build()

	}

	private fun handleData(data: String) {

		if (scan_once) {
			if (!hasScanned) {
				qrEader.stop()
				hasScanned = true
				Log.d("ajashdfkjahsdf", data)
				Log.d("ajashdfkjahsdf", scan_once.toString())
				(activity as QRDataReceiver).handleData(data)
				qrEader.releaseAndCleanup()
			}
		} else {
			val scanTime = System.currentTimeMillis()
			if (data != lastScanValue || (scanTime - lastScanTime) > SCAN_DELAY) {
				(activity as QRDataReceiver).handleData(data)
				lastScanTime = scanTime
				lastScanValue = data
			}
//			qrEader.start()
		}
	}

	override fun onResume() {
		super.onResume()

		qrEader.initAndStart(camera_view)
	}

	override fun onPause() {
		super.onPause()
		qrEader.releaseAndCleanup()
	}

	override fun onStop() {
		super.onStop()
		qrEader.stop()
	}

	companion object {
		@JvmStatic
		fun newInstance(scan_once: Boolean) =
				QRFragment().apply {
					arguments = Bundle().apply {
						putBoolean(SCAN_ONCE, scan_once)
					}
				}

		fun newInstance() =
				QRFragment().apply {
					arguments = Bundle().apply {
						putBoolean(SCAN_ONCE, true)
					}
				}
	}
}
