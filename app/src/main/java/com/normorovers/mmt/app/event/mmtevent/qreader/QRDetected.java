package com.normorovers.mmt.app.event.mmtevent.qreader;

public interface QRDetected {

	/**
	 * On detected.
	 *
	 * @param payload
	 *     the data
	 */
	// Called from not main thread. Be careful
	void onDetected(QRAction payload);

	/**
	 * On error.
	 */
	// Called from not main thread. Be careful
	void onError();
}