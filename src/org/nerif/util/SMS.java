package org.nerif.util;

import java.util.List;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMS {
	private static SMS instance = null;

	public static SMS getInstance() {
		if (instance == null) {
			instance = new SMS();

			Twilio.init(Config.SMS_ACCOUNT_SID, Config.SMS_AUTH_TOKEN);
		}
		return instance;
	}

	public void sendFromTwilio(List<String> to, String data, String descricao, String num) {
		final String body = Config.SMS_BODY.replace("%indicador%", descricao).replace("%data%", data).replace("%vezes%",
				num);

		to.forEach(v -> {
			Message.creator(new PhoneNumber(v), new PhoneNumber(Config.SMS_PHONE_NUMBER), body).create();
		});
	}
}
