package org.nerif.parser;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nerif.estatistica.EstatisticaArquivo;
import org.nerif.model.FormatoLog;
import org.nerif.model.InfoPropriedade;
import org.nerif.modulos.ModuloEstatistico;
import org.nerif.modulos.ModuloSimples;
import org.nerif.util.Config;

public class ApacheParser {
	private HashMap<String, HashMap<Integer, InfoPropriedade>> arquivosIndices = new HashMap<>();
	private HashMap<String, Long> arquivosParaAnalise = new HashMap<>();
	private boolean esperaArquivosExistentes = false;

	public void run() throws Exception {
		analisaArquivosExistentes();
		if (!esperaArquivosExistentes)
			analisaArquivos();
	}

	private void analisaArquivosExistentes() throws Exception {
		Files.walk(Paths.get(Config.caminhoLog)).parallel().filter(file -> file.toFile().getName().endsWith(".log"))
				.forEach(f -> {
					esperaArquivosExistentes = true;
					Config.activeThreads++;
					Config.THREAD_POOL_EXECUTOR.submit(new ArquivoExistenteSimples(f.toFile()));
				});

		if (Config.EXECUTA_MODULO_ESTATISTICO) {
			Files.walk(Paths.get(Config.caminhoLog)).parallel().filter(file -> file.toFile().getName().endsWith(".log"))
					.forEach(f -> {
						esperaArquivosExistentes = true;
						Config.activeThreads++;
						Config.THREAD_POOL_EXECUTOR.submit(new ArquivoExistenteEstatistico(f.toFile()));
					});
		}

	}

	@SuppressWarnings("unchecked")
	private void analisaArquivos() {

		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			Path dir = Paths.get(Config.caminhoLog);

			final ModuloSimples moduloSimples = new ModuloSimples();
			for (;;) {
				WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if (kind == OVERFLOW) {
						continue;
					}

					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path path = dir.resolve(ev.context());
					File file = path.toFile();
					if (file.getName().endsWith(".log")) {
						Map<String, Object> map = Files.readAttributes(path, "*");
						Long tamanhoAntigo = arquivosParaAnalise.get(file.getName());
						long valorAtual = (Long) map.get(Config.ARQUIVO_SIZE);
						arquivosParaAnalise.put(file.getName(), valorAtual);
						if (tamanhoAntigo == null) {
							tamanhoAntigo = 0l;
						}
						if (valorAtual == tamanhoAntigo)
							continue;
						RandomAccessFile raf = new RandomAccessFile(file, "r");
						raf.seek(tamanhoAntigo);
						int tamanhoFinal = (int) (valorAtual - tamanhoAntigo);
						byte[] buff = new byte[tamanhoFinal];
						raf.read(buff, 0, tamanhoFinal);

						BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buff)));
						Iterator<String> iterator = br.lines().parallel().iterator();
						while (iterator.hasNext()) {
							String line = iterator.next();
							if (line.startsWith("#")) {
								if (line.startsWith("#Fields:")) {
									HashMap<Integer, InfoPropriedade> translateMap = new HashMap<>();
									String[] splitFields = line.split(Config.WHITESPACE);
									for (FormatoLog formato : Config.colunasLog) {
										for (int i = 1; i != splitFields.length; i++) {
											if (formato.getChave().equals(splitFields[i])) {
												translateMap.put(i - 1, formato.getInfoPropriedade());
												continue;
											}
										}
									}
									arquivosIndices.put(file.getName(), translateMap);
								}
							} else {
								HashMap<Integer, InfoPropriedade> translateMap = arquivosIndices.get(file.getName());
								String[] splitFields = line.split(Config.WHITESPACE);
								final HashMap<String, String> coluns = new HashMap<>(Config.colunasLog.size());
								try {
									translateMap.forEach((k, v) -> {
										if (v.name().equals(InfoPropriedade.DATA.name())) {
											coluns.put(v.getInfoPropriedade(), splitFields[k].substring(1));
										} else {
											coluns.put(v.getInfoPropriedade(), splitFields[k]);
										}
									});
									moduloSimples.processaLinha(coluns);
									arquivosParaAnalise.put(file.getName(), (Long) map.get(Config.ARQUIVO_SIZE));
									ModuloEstatistico.getInstance().processaLinha(coluns);
								} catch (Exception e) {
								}
							}
						}
						raf.close();
					}
				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
										continue;
									}
								}
							}
							Config.lock.lock();
							arquivosIndices.put(f.getName(), translateMap);
							Config.lock.unlock();
						}
					} else {
						String[] splitFields = line.split(Config.WHITESPACE);
						final HashMap<String, String> coluns = new HashMap<>(Config.colunasLog.size());
						String duracao = splitFields[9];
						if (!duracao.equals("-")) {
							try {
								translateMap.forEach((k, v) -> {
									if (v.name().equals(InfoPropriedade.DATA.name())) {
										coluns.put(v.getInfoPropriedade(), splitFields[k].substring(1));
									} else {
										coluns.put(v.getInfoPropriedade(), splitFields[k]);
									}
								});
								moduloSimples.processaLinha(coluns);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				Map<String, Object> map = Files.readAttributes(f.toPath(), "*");
				arquivosParaAnalise.put(f.getName(), (Long) map.get(Config.ARQUIVO_SIZE));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (--Config.activeThreads == 0) {
					analisaArquivos();
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
										continue;
									}
								}
							}
						}
					} else {
						String[] splitFields = line.split(Config.WHITESPACE);
						final HashMap<String, String> coluns = new HashMap<>(Config.colunasLog.size());
						String duracao = splitFields[9];
						if (!duracao.equals("-")) {
							try {
								translateMap.forEach((k, v) -> {
									if (v.name().equals(InfoPropriedade.DATA.name())) {
										coluns.put(v.getInfoPropriedade(), splitFields[k].substring(1));
									} else {
										coluns.put(v.getInfoPropriedade(), splitFields[k]);
									}
								});
								estatisticaArquivo.processaLinha(coluns);
							} catch (Exception e) {
							}
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				ModuloEstatistico.getInstance().mesclaArquivo(estatisticaArquivo);
				if (--Config.activeThreads == 0) {
					analisaArquivos();
				}
			}
		}

	}
}
