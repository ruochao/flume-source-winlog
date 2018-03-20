package com.mocircle.flume.source.config;

import com.mocircle.jwinlog.AuthMethod;

public enum AuthMethodConfig {

	DEFAULT(AuthMethod.DEFAULT),

	NEGOTIATE(AuthMethod.NEGOTIATE),

	KERBEROS(AuthMethod.KERBEROS),

	NTLM(AuthMethod.NTLM);

	private int value;

	private AuthMethodConfig(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
