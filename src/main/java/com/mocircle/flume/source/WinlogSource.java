package com.mocircle.flume.source;

import org.apache.flume.source.AbstractSource;

public class WinlogSource extends AbstractSource {

	public static final String KEY_REMOTE = "remote";
	public static final String KEY_SERVER = "server";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_DOMAIN = "domain";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_AUTH_METHOD = "authMethod";
	public static final String KEY_EVENT_CHANNELS = "eventChannels";
	public static final String KEY_RETRIEVE_MODE = "retrieveMode";
	public static final String KEY_START_RECORD = "startRecord";
	public static final String KEY_POSITION_FILE = "positionFile";

}
