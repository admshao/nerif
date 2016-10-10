package org.nerif;

import java.io.IOException;

import org.nerif.parser.IISParser;
import org.nerif.util.Config;

public class Main {
	public static void main(String[] args) {
		try {
			Config.initConfig();
			IndicadoresSimples.getInstance().init();
			switch(Config.tipoServidor) {
				case "iis":
					IISParser parser = new IISParser();
					parser.run();
				default:
					break;
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
