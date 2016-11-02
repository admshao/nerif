package org.nerif;

import java.io.IOException;

import org.nerif.parser.IISParser;
import org.nerif.util.Config;

public class Main {
	public static void main(String[] args) {
		try {
			Config.initConfig();
			switch (Config.tipoServidor) {
			case "iis":
				IISParser parser = new IISParser();
				parser.run();
			default:
				break;
			}
			
			Config.THREAD_POOL_EXECUTOR.shutdown();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
