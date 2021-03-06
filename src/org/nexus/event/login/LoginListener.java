package org.nexus.event.login;

import org.osbot.rs07.constants.ResponseCode;
import org.osbot.rs07.listener.LoginResponseCodeListener;
import org.osbot.rs07.script.Script;

public class LoginListener implements LoginResponseCodeListener {
	Script context;

	public LoginListener(Script context) {
		this.context = context;
	}

	@Override
	public void onResponseCode(int response) throws InterruptedException {
		context.log("respone code: " + response);
		if (response == ResponseCode.ACCOUNT_DISABLED) {
			context.log("BANNED");
		}

	}

}