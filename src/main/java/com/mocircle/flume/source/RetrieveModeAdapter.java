package com.mocircle.flume.source;

import com.mocircle.jwinlog.EventRetrieveMode;

public enum RetrieveModeAdapter {

	OLDEST(EventRetrieveMode.FROM_OLDEST),

	RECORD(EventRetrieveMode.AFTER_RECORD_ID),

	FUTURE(EventRetrieveMode.FROM_FUTURE);

	private int value;

	private RetrieveModeAdapter(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
