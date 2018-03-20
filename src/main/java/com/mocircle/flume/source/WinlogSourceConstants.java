package com.mocircle.flume.source;

import com.mocircle.flume.source.config.AuthMethodConfig;
import com.mocircle.flume.source.config.RetrieveModeConfig;

public interface WinlogSourceConstants {

	//
	// Configuration keys
	//

	/**
	 * Indicate the windows event log is from remote computer or local computer
	 */
	String REMOTE = "remote";

	/**
	 * Remote computer name
	 */
	String SERVER = "server";

	/**
	 * User name for remote computer
	 */
	String USERNAME = "username";

	/**
	 * Domain of remote computer
	 */
	String DOMAIN = "domain";

	/**
	 * User password for remote computer
	 */
	String PASSWORD = "password";

	/**
	 * Authentication method for log in remote computer. Options are "default",
	 * "negotiate", "kerberos", "ntlm".
	 */
	String AUTH_METHOD = "authMethod";

	/**
	 * Indicate which event channels need to be consumed, separate by comma.
	 * e.g. "application, security"
	 */
	String EVENT_CHANNELS = "eventChannels";

	/**
	 * Indicate how to retrieve the event log. Options are "oldest", "record",
	 * "future".
	 */
	String RETRIEVE_MODE = "retrieveMode";

	/**
	 * Only applies if retrieveMode is record.
	 */
	String START_RECORD_IDS = "startRecordIds";

	/**
	 * The file path which records the event status/position.
	 */
	String RECORD_STATUS_FILE = "recordStatusFile";

	//
	// Default values for configuration
	//

	boolean DEFAULT_REMOTE = false;

	String DEFAULT_AUTH_METHOD = AuthMethodConfig.DEFAULT.name();

	String DEFAULT_RETRIEVE_MODE = RetrieveModeConfig.OLDEST.name();

	long DEFAULT_START_RECORD_ID = 0;
	
	String DEFAULT_RECORD_STATUS_FILE = "records.status";

}
