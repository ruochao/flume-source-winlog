package com.mocircle.flume.source;

import java.io.IOException;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractPollableSource;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mocircle.flume.source.config.AuthMethodConfig;
import com.mocircle.flume.source.config.RetrieveModeConfig;
import com.mocircle.flume.source.utils.ConfigUtils;

public class WinlogSource extends AbstractPollableSource implements Configurable, WinlogSourceConstants {

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

	private ReliableWinlogEventReader eventReader;

	public WinlogSource() {
	}

	@Override
	protected void doConfigure(Context context) throws FlumeException {

		remote = context.getBoolean(REMOTE, DEFAULT_REMOTE);
		if (remote) {
			server = context.getString(SERVER);
			userName = context.getString(USERNAME);
			domain = context.getString(DOMAIN);
			password = context.getString(PASSWORD);
			try {
				authMethod = AuthMethodConfig
						.valueOf(context.getString(AUTH_METHOD, DEFAULT_AUTH_METHOD).toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new FlumeException(
						"Unsupported authMethod: " + context.getString(AUTH_METHOD, DEFAULT_AUTH_METHOD));
			}
		}

		String eventChannelNames = context.getString(EVENT_CHANNELS, "");
		Preconditions.checkState(!Strings.isNullOrEmpty(eventChannelNames.trim()),
				"You must define at least one event log channel");
		eventChannels = ConfigUtils.parseAsStringArray(eventChannelNames, ",");

		try {
			retrieveMode = RetrieveModeConfig
					.valueOf(context.getString(RETRIEVE_MODE, DEFAULT_RETRIEVE_MODE).toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new FlumeException(
					"Unsupported retrieveMode: " + context.getString(RETRIEVE_MODE, DEFAULT_RETRIEVE_MODE));
		}

		if (retrieveMode == RetrieveModeConfig.RECORD) {
			String startRecordIdsStr = context.getString(START_RECORD_IDS, "");
			startRecordIds = ConfigUtils.parseAsLongArray(startRecordIdsStr, ",");
		}

		String homePath = System.getProperty("user.home").replace('\\', '/');
		recordStatusFile = context.getString(RECORD_STATUS_FILE, homePath + DEFAULT_RECORD_STATUS_FILE);

		buildEventReader();
	}

	@Override
	protected void doStart() throws FlumeException {

	}

	@Override
	protected void doStop() throws FlumeException {

	}

	@Override
	protected Status doProcess() throws EventDeliveryException {
		for (String channel : eventChannels) {
			try {
				eventReader.setCurrentEventChannel(channel);
				eventReader.readEvents(10);
			} catch (IOException e) {

				return Status.BACKOFF;
			}
		}
		return Status.READY;
	}

	private void buildEventReader() {
		eventReader = new ReliableWinlogEventReader.Builder() //
				.withSourceConfig(remote, server, userName, domain, password, authMethod) //
				.withEventConfig(eventChannels, retrieveMode, startRecordIds) //
				.withReaderConfig(recordStatusFile) //
				.build();
	}

}
