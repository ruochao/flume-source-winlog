package com.mocircle.flume.source.config;

import com.mocircle.jwinlog.EventRetrieveMode;

public enum RetrieveModeConfig {

	OLDEST(EventRetrieveMode.FROM_OLDEST),

	RECORD(EventRetrieveMode.AFTER_RECORD_ID),

	FUTURE(EventRetrieveMode.FROM_FUTURE);

	private int value;

	private RetrieveModeConfig(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
