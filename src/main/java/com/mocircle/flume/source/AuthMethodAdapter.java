package com.mocircle.flume.source;

import com.mocircle.jwinlog.AuthMethod;

public enum AuthMethodAdapter {

	DEFAULT(AuthMethod.DEFAULT),

	NEGOTIATE(AuthMethod.NEGOTIATE),

	KERBEROS(AuthMethod.KERBEROS),

	NTLM(AuthMethod.NTLM);

	private int value;

	private AuthMethodAdapter(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
