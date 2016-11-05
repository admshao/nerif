package org.nerif.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.nerif.ModuloSimples;
import org.nerif.model.FormatoLog;
import org.nerif.util.Config;

public class IISParser {

	public void run() throws IOException {
		Files.walk(Paths.get(Config.caminhoLog)).parallel()
				.filter(file -> file.toFile().isFile() && file.toFile().getName().endsWith(".log"))
				.forEach(f -> Config.THREAD_POOL_EXECUTOR.submit(new FileRunnable(f.toFile())));

	}

	public class FileRunnable implements Runnable {
		private final File f;

		public FileRunnable(final File file) {
			f = file;
		}

		@Override
		public void run() {
			final ModuloSimples indicadorSimples = new ModuloSimples();
			int lastIndex = 0;
			ArrayList<Integer> columnIndex = new ArrayList<>();
			try {
				Iterator<String> iterator = Files.lines(f.toPath()).parallel().iterator();
				while (iterator.hasNext()) {
					String line = iterator.next();
					if (line.startsWith("#")) {
						if (line.startsWith("#Fields:")) {
							columnIndex = new ArrayList<>();
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
							final ArrayList<String> coluns = new ArrayList<>(columnIndex.size());
							for (int i : columnIndex) {
								coluns.add(splitFields[i]);
							}
							indicadorSimples.processaLinha(coluns);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
