package org.nerif;

import org.nerif.parser.IISParser;
import org.nerif.util.Config;

public class Main {
	public static void main(String[] args) {
		try {
			Config.initConfig(args);

			//Email.getInstance();
			//SMS.getInstance();
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
				//Email.getInstance().close();
				ModuloAlerta.getInstance().gerarRelatorio();
			}
		});
	}

}
