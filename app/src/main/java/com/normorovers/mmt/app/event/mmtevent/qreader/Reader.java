package com.normorovers.mmt.app.event.mmtevent.qreader;

import android.content.Context;
import android.view.SurfaceView;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class Reader {
	private QREader qrEader = null;
	private SurfaceView surface = null;

	private long coolOffTime = 0;

	public void onCreate(Context context, SurfaceView surface, final QRDetected operator) {
		this.surface = surface;

		qrEader = new QREader.Builder(context, surface, new QRDataListener() {
			@Override
			public void onDetected(String data) {
				// use coolOffTime to only call the callback after a delay of 800ms from the previous callback
				if ((System.currentTimeMillis() - coolOffTime) > 800) {
					coolOffTime = System.currentTimeMillis();

					String[] splitData = data.split(":");

					StringBuilder finalData = new StringBuilder();

					if (splitData.length > 3) {
						for (int i = 2; i < splitData.length; i++) {
							finalData.append(splitData[i]);
							if (i < splitData.length - 1) finalData.append(":");
						}
					} else {
						finalData = new StringBuilder(splitData[2]);
					}

 					operator.onDetected(new QRAction(splitData[1], finalData.toString()));
				}
			}
		}).facing(QREader.BACK_CAM)
				.enableAutofocus(true)
				.height(surface.getHeight())
				.width(surface.getWidth())
				.build();
	}

	public void onResume() {
		qrEader.initAndStart(surface);
	}

	public void onPause() {
		qrEader.releaseAndCleanup();
	}

	public boolean isRunning() {
		return qrEader.isCameraRunning();
	}

	public void start() {
		qrEader.start();
	}

	public void stop() {
		qrEader.stop();
	}
}

