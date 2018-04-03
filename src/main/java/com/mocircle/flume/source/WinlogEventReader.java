package com.mocircle.flume.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mocircle.flume.source.config.AuthMethodConfig;
import com.mocircle.flume.source.config.RetrieveModeConfig;
import com.mocircle.jwinlog.EventEntry;
import com.mocircle.jwinlog.EventEntryIterator;
import com.mocircle.jwinlog.EventLogFactory;
import com.mocircle.jwinlog.EventLogManager;
import com.mocircle.jwinlog.RenderOption;

public class WinlogEventReader {

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

		public WinlogEventReader build() {
			return new WinlogEventReader(remote, server, userName, domain, password, authMethod, eventChannels,
					retrieveMode, startRecordIds, recordStatusFile);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(WinlogEventReader.class);

	private String[] eventChannels;
	private RetrieveModeConfig retrieveMode;

	private EventLogManager eventLogManager;
	private Map<String, EventEntryIterator> logIterators;
	private RecordStatusIO recordStatus;

	private WinlogEventReader(boolean remote, String server, String userName, String domain, String password,
			AuthMethodConfig authMethod, String[] eventChannels, RetrieveModeConfig retrieveMode, long[] startRecordIds,
			String recordStatusFile) {

		this.eventChannels = eventChannels;
		this.retrieveMode = retrieveMode;
		if (remote) {
			eventLogManager = EventLogFactory.createRemoteEventLogManager(server, userName, domain, password,
					authMethod.getValue());
		} else {
			eventLogManager = EventLogFactory.createLocalEventLogManager();
		}
		recordStatus = new RecordStatusIO(recordStatusFile, buildStartRecordIdMap(eventChannels, startRecordIds));
		rebuildIterators(recordStatus.readRecords());
	}

	public List<Event> readEvents(String eventChannel, int n) throws IOException {
		List<Event> events = Lists.newLinkedList();
		EventEntryIterator iterator = logIterators.get(eventChannel);
		if (iterator != null) {
			for (int i = 0; i < n; i++) {
				if (!iterator.hasNext()) {
					break;
				}
				EventEntry entry = iterator.next();
				Event event = WinlogEventBuilder.withLogEntry(entry);
				// System.out.println(new String(event.getBody()));
				events.add(event);
				System.out.println(eventChannel + ": " + entry.getRecordId());
			}
		}
		return events;
	}

	public void commitInMemory() throws IOException {
		recordStatus.updateRecord(logIterators.values());
	}

	public void rollback() {
		rebuildIterators(recordStatus.readRecords());
	}

	public void persistCommit() {
		recordStatus.commitRecord();
	}

	public void close() throws IOException {
		for (EventEntryIterator iterator : logIterators.values()) {
			// iterator.close();

		}
	}

	private Map<String, Long> buildStartRecordIdMap(String[] channels, long[] startRecordIds) {
		Map<String, Long> map = new HashMap<>();
		for (int i = 0; i < channels.length; i++) {
			if (startRecordIds == null) {
				map.put(channels[i], WinlogSourceConstants.DEFAULT_START_RECORD_ID);
			} else {
				map.put(channels[i],
						startRecordIds.length > i ? startRecordIds[i] : WinlogSourceConstants.DEFAULT_START_RECORD_ID);
			}
		}
		return map;
	}

	private void rebuildIterators(Map<String, Long> startRecordIds) {
		logIterators = new HashMap<>();
		for (int i = 0; i < eventChannels.length; i++) {
			String channel = eventChannels[i];
			Long startRecordId = startRecordIds.get(channel);
			RetrieveModeConfig localRetrieveMode = startRecordId == null ? retrieveMode : RetrieveModeConfig.RECORD;
			RenderOption option = new RenderOption();
			option.setRenderEventXml(false);
			EventEntryIterator iterator = eventLogManager.retrieveEventLogs(channel, localRetrieveMode.getValue(),
					startRecordId, option);
			logIterators.put(channel, iterator);
		}
	}

}
