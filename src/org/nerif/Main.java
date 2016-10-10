package org.nerif;

import java.io.IOException;

import org.nerif.util.Config;

public class Main {
	public static void main(String[] args) {
		try {
			Config.initConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
