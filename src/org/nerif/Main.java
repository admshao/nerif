package org.nerif;

import org.nerif.modulos.ModuloAlerta;
import org.nerif.modulos.ModuloAnalise;
import org.nerif.parser.ApacheParser;
import org.nerif.parser.IISParser;
import org.nerif.util.Config;
import org.nerif.util.Email;

public class Main {
	public static void main(String[] args) {
		try {
			Config.initConfig(args);

			ModuloAlerta.getInstance();
			ModuloAnalise.getInstance();

			switch (Config.tipoServidor) {
			case "iis":
				IISParser parser = new IISParser();
				parser.run();
				break;
			case "apache":
				ApacheParser apache = new ApacheParser();
				apache.run();
				break;
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
