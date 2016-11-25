package org.nerif.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

import org.nerif.EstatisticaArquivo;
import org.nerif.ModuloEstatistico;
import org.nerif.ModuloSimples;
import org.nerif.model.FormatoLog;
import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class IISParser {

	public void run() throws IOException {
		Files.walk(Paths.get(Config.caminhoLog)).parallel()
				.filter(file -> file.toFile().isFile() && file.toFile().getName().endsWith(".log")).forEach(f -> {
					Config.activeThreads++;
					Config.THREAD_POOL_EXECUTOR.submit(new ArquivoExistenteSimples(f.toFile()));
				});

		if (Config.EXECUTA_MODULO_ESTATISTICO) {
			Files.walk(Paths.get(Config.caminhoLog)).parallel()
					.filter(file -> file.toFile().isFile() && file.toFile().getName().endsWith(".log")).forEach(f -> {
						Config.activeThreads++;
						Config.THREAD_POOL_EXECUTOR.submit(new ArquivoExistenteEstatistico(f.toFile()));
					});
		}

		Config.THREAD_POOL_EXECUTOR.shutdown();

		/*
		 * WatchService watcher = FileSystems.getDefault().newWatchService();
		 * Path dir = Paths.get(Config.caminhoLog); for (;;) { WatchKey key; try
		 * { key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY); } catch
		 * (IOException x) { return; }
		 * 
		 * for (WatchEvent<?> event : key.pollEvents()) { WatchEvent.Kind<?>
		 * kind = event.kind(); System.out.println(kind.name());
		 * 
		 * if (kind == OVERFLOW) { continue; }
		 * 
		 * WatchEvent<Path> ev = (WatchEvent<Path>) event; Path filename =
		 * ev.context();
		 * 
		 * Path path = dir.resolve(filename); File file = path.toFile();
		 * System.out.println(file); // if (kind == ENTRY_MODIFY) { if
		 * (file.getName().endsWith(".log")) { Map<String, Object> map =
		 * Files.readAttributes(path, "*"); System.out.println(map); } // } }
		 * 
		 * boolean valid = key.reset(); if (!valid) { break; } }
		 */

	}

	public class ArquivoExistenteSimples implements Runnable {
		private final File f;

		public ArquivoExistenteSimples(final File file) {
			f = file;
		}

		@Override
		public void run() {
			try {
				final ModuloSimples moduloSimples = new ModuloSimples();
				int lastIndex = 0;
				HashMap<Integer, InfoPropriedade> translateMap = new HashMap<>();
				Iterator<String> iterator = Files.lines(f.toPath()).parallel().iterator();
				while (iterator.hasNext()) {
					String line = iterator.next();
					if (line.startsWith("#")) {
						if (line.startsWith("#Fields:")) {
							translateMap = new HashMap<>();
							String[] splitFields = line.split(Config.WHITESPACE);
							for (FormatoLog formato : Config.colunasLog) {
								for (int i = 1; i != splitFields.length; i++) {
									if (formato.getChave().equals(splitFields[i])) {
										translateMap.put(i - 1, formato.getInfoPropriedade());
										lastIndex = i - 1;
										continue;
									}
								}
							}
						}
					} else {
						String[] splitFields = line.split(Config.WHITESPACE);
						if (lastIndex + 1 <= splitFields.length) {
							final HashMap<String, String> coluns = new HashMap<>(Config.colunasLog.size());
							translateMap.forEach((k, v) -> {
								coluns.put(v.getInfoPropriedade(), splitFields[k]);
							});
							moduloSimples.processaLinha(coluns);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (--Config.activeThreads == 0) {
					Config.TIMER.cancel();
				}
			}
		}

	}

	public class ArquivoExistenteEstatistico implements Runnable {
		private final File f;

		public ArquivoExistenteEstatistico(final File file) {
			f = file;
		}

		@Override
		public void run() {
			final EstatisticaArquivo estatisticaArquivo = new EstatisticaArquivo();
			try {
				int lastIndex = 0;
				HashMap<Integer, InfoPropriedade> translateMap = new HashMap<>();
				Iterator<String> iterator = Files.lines(f.toPath()).parallel().iterator();
				while (iterator.hasNext()) {
					String line = iterator.next();
					if (line.startsWith("#")) {
						if (line.startsWith("#Fields:")) {
							translateMap = new HashMap<>();
							String[] splitFields = line.split(Config.WHITESPACE);
							for (FormatoLog formato : Config.colunasLog) {
								for (int i = 1; i != splitFields.length; i++) {
									if (formato.getChave().equals(splitFields[i])) {
										translateMap.put(i - 1, formato.getInfoPropriedade());
										lastIndex = i - 1;
										continue;
									}
								}
							}
						}
					} else {
						String[] splitFields = line.split(Config.WHITESPACE);
						if (lastIndex + 1 <= splitFields.length) {
							final HashMap<String, String> coluns = new HashMap<>(Config.colunasLog.size());
							translateMap.forEach((k, v) -> {
								coluns.put(v.getInfoPropriedade(), splitFields[k]);
							});
							estatisticaArquivo.processaLinha(coluns);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				ModuloEstatistico.getInstance().mesclaArquivo(estatisticaArquivo);
				if (--Config.activeThreads == 0) {
					Config.TIMER.cancel();
				}
			}
		}

	}

}
