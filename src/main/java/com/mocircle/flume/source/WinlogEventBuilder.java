package com.mocircle.flume.source;

import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;

import com.mocircle.jwinlog.EventEntry;

public class WinlogEventBuilder {

	public static Event withLogEntry(EventEntry entry) {
		String body = entry.getEventRaw();
		Map<String, String> header = new HashMap<>();
		return EventBuilder.withBody(body.getBytes(), header);
	}

}
