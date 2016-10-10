package org.nerif.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.nerif.IndicadoresSimples;
import org.nerif.model.FormatoLog;
import org.nerif.util.Config;

public class IISParser {

	public void run() {
		for (File f : Paths.get(Config.caminhoLog).toFile().listFiles()) {
			if (f.isFile()) {
				int lastIndex = 0;
				ArrayList<Integer> columnIndex = new ArrayList<>();
				String line;
				try (BufferedReader br = new BufferedReader(new FileReader(f))) {
					while ((line = br.readLine()) != null) {
						if (line.startsWith("#")) {
							if (line.startsWith("#Fields:")) {
								String[] splitFields = line.split(Config.WHITESPACE);
								for (FormatoLog formato : Config.colunasLog) {
									for (int i = 1; i != splitFields.length; i++) {
										if (formato.getChave().equals(splitFields[i])) {
											columnIndex.add(i - 1);
											continue;
										}
									}
								}
								lastIndex = columnIndex.get(columnIndex.size() - 1);
							}
						} else {
							String[] splitFields = line.split(Config.WHITESPACE);
							if (lastIndex + 1 <= splitFields.length) {
								List<String> coluns = new ArrayList<>();
								for (int i : columnIndex) {
									coluns.add(splitFields[i]);
								}
								IndicadoresSimples.getInstance().processaNovaLinha(coluns);
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
