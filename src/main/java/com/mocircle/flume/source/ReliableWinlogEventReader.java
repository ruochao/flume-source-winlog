package com.mocircle.flume.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Event;
import org.apache.flume.client.avro.ReliableEventReader;

import com.google.common.collect.Lists;
import com.mocircle.flume.source.config.AuthMethodConfig;
import com.mocircle.flume.source.config.RetrieveModeConfig;
import com.mocircle.jwinlog.EventEntryIterator;
import com.mocircle.jwinlog.EventLogFactory;
import com.mocircle.jwinlog.EventLogManager;
import com.mocircle.jwinlog.RenderOption;

public class ReliableWinlogEventReader implements ReliableEventReader {

	public static class Builder {

		private boolean remote;
		private String server;
		private String userName;
		private String domain;
		private String password;
		private AuthMethodConfig authMethod;
		private String[] eventChannels;
		private RetrieveModeConfig retrieveMode;
		private long[] startRecordIds;
		private String recordStatusFile;

		public Builder withSourceConfig(boolean remote, String server, String userName, String domain, String password,
				AuthMethodConfig authMethod) {
			this.remote = remote;
			this.server = server;
			this.userName = userName;
			this.domain = domain;
			this.password = password;
			this.authMethod = authMethod;
			return this;
		}

		public Builder withEventConfig(String[] eventChannels, RetrieveModeConfig retrieveMode, long[] startRecordIds) {
			this.eventChannels = eventChannels;
			this.retrieveMode = retrieveMode;
			this.startRecordIds = startRecordIds;
			return this;
		}

		public Builder withReaderConfig(String recordStatusFile) {
			this.recordStatusFile = recordStatusFile;
			return this;
		}

		public ReliableWinlogEventReader build() {
			return new ReliableWinlogEventReader(remote, server, userName, domain, password, authMethod, eventChannels,
					retrieveMode, startRecordIds, recordStatusFile);
		}
	}

	private String[] eventChannels;
	private String recordStatusFile;
	private Map<String, EventEntryIterator> logIterators;
	private String currentEventChannel;

	private ReliableWinlogEventReader(boolean remote, String server, String userName, String domain, String password,
			AuthMethodConfig authMethod, String[] eventChannels, RetrieveModeConfig retrieveMode, long[] startRecordIds,
			String recordStatusFile) {

		this.eventChannels = eventChannels;
		this.recordStatusFile = recordStatusFile;

		EventLogManager eventLogManager = null;
		if (remote) {
			eventLogManager = EventLogFactory.createRemoteEventLogManager(server, userName, domain, password,
					authMethod.getValue());
		} else {
			eventLogManager = EventLogFactory.createLocalEventLogManager();
		}
		logIterators = new HashMap<>();
		for (int i = 0; i < eventChannels.length; i++) {
			String channel = eventChannels[i];
			Long startRecordId = null;
			if (startRecordIds != null) {
				startRecordId = startRecordIds.length > i ? startRecordIds[i]
						: WinlogSourceConstants.DEFAULT_START_RECORD_ID;
			}

			RenderOption option = new RenderOption();
			option.setRenderEventXml(false);
			EventEntryIterator iterator = eventLogManager.retrieveEventLogs(channel, retrieveMode.getValue(),
					startRecordId, option);
			logIterators.put(channel, iterator);
		}
	}

	public void setCurrentEventChannel(String currentEventChannel) {
		this.currentEventChannel = currentEventChannel;
	}

	@Override
	public Event readEvent() throws IOException {
		List<Event> events = readEvents(1);
		if (events.isEmpty()) {
			return null;
		}
		return events.get(0);
	}

	@Override
	public List<Event> readEvents(int n) throws IOException {
		List<Event> events = Lists.newLinkedList();
		EventEntryIterator iterator = logIterators.get(currentEventChannel);
		if (iterator != null) {
			for (int i = 0; i < n; i++) {
				if (!iterator.hasNext()) {
					break;
				}
				Event event = WinlogEventBuilder.withLogEntry(iterator.next());
				System.out.println(new String(event.getBody()));
				events.add(event);
			}
		}
		return events;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void commit() throws IOException {

	}

}
