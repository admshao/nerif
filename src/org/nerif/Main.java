package org.nerif;

import org.nerif.parser.IISParser;
import org.nerif.util.Config;
import org.nerif.util.Email;

public class Main {
	public static void main(String[] args) {
		try {
			Config.initConfig(args);

			ModuloAlerta.getInstance().init();

			switch (Config.tipoServidor) {
			case "iis":
				IISParser parser = new IISParser();
				parser.run();
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (Config.EMAIL_ALERT) {
					Email.getInstance().close();
				}
			}
		});
	}

}
