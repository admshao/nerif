package org.nerif;

import java.io.IOException;

import org.nerif.parser.IISParser;
import org.nerif.util.Config;
import org.nerif.util.Email;
import org.nerif.util.SMS;

public class Main {
	public static void main(String[] args) {
		long start = System.nanoTime();

		try {
			Config.initConfig();

			Email.getInstance();
			SMS.getInstance();
			ModuloAlerta.getInstance().init();

			switch (Config.tipoServidor) {
			case "iis":
				IISParser parser = new IISParser();
				parser.run();
			default:
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Email.getInstance().close();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				ModuloAlerta.getInstance().dump();
				System.out.println("Tempo total -> " + ((System.nanoTime() - start) / 1000000000) + "s");
			}
		});
	}

}
