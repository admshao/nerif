package org.nerif.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nerif.gson.Gson;
import org.nerif.gson.JsonArray;
import org.nerif.gson.JsonElement;
import org.nerif.gson.JsonObject;
import org.nerif.model.ConcurrentDateFormat;
import org.nerif.model.ConcurrentDateTimeFormat;
import org.nerif.model.ConcurrentTimeFormat;
import org.nerif.model.FormatoLog;
import org.nerif.model.Grupo;
import org.nerif.model.Indicador;
import org.nerif.model.InfoPropriedade;
import org.nerif.model.Regra;
import org.nerif.model.TipoComparacao;
import org.nerif.model.TipoValor;
import org.nerif.model.Usuario;

public class Config {
	public static final Gson GSON = new Gson();
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String WHITESPACE = " ";
	public static final ConcurrentDateFormat dfData = new ConcurrentDateFormat();
	public static final ConcurrentTimeFormat dfHora = new ConcurrentTimeFormat();
	public static final ConcurrentDateTimeFormat dfDataHora = new ConcurrentDateTimeFormat();
	
	public static final ExecutorService THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public static final Timer TIMER = new Timer();
	public static volatile int activeThreads = 0;
	
	public static String EMAIL_USERNAME;
	public static String EMAIL_PASSWORD;
	public static final String EMAIL_FROM = "Nerif - Sistema de Monitoramento";
	public static final String EMAIL_SUBJECT = "Alerta de Indicador Disparado!";
	public static final String EMAIL_BODY = "<!doctype html><html><head><title></title></head><body><h1>Atencao</h1><p>Foi "
			+ "detectada uma violacao de um indicador vinculado a um grupo no qual voce faz parte.</p><p><strong>Descricao "
			+ "do Indicador:</strong></p><p>%indicador%</p><p><strong>Data e Hora da Deteccao:</strong></p><p>%data%</p>"
			+ "<p><strong>Numero de vezes que este alerta foi disparado hoje:</strong></p><p>%vezes%</p></body></html>";
	
	public static String SMS_ACCOUNT_SID;
	public static String SMS_AUTH_TOKEN;
	public static String SMS_PHONE_NUMBER;
	public static final String SMS_BODY = "Atencao. Foi detectada uma violacao de um indicador vinculado a um grupo no qual voce"
			+ " faz parte. Descricao do Indicador: %indicador%. Data e Hora da Deteccao: %data%. Numero de vezes que este alerta "
			+ "foi disparado hoje: %vezes%.";
	
	public static final Random RANDOM = new Random(System.nanoTime());
	private static final URI URI_CONFIG = URI
			.create("file://" + Paths.get("").toAbsolutePath().toString() + "/client/config/config.json"); // ESTA LINHA PARA ECLIPSE
			//.create("file://" + Paths.get("").toAbsolutePath().toString() + "/../client/config/config.json"); // ESTA LINHA PARA BUILDS

	public static String tipoServidor;
	public static String caminhoLog;
	
	public static List<FormatoLog> colunasLog = new ArrayList<>();
	public static HashMap<Integer, Usuario> usuarios = new HashMap<>();
	public static HashMap<Integer, Indicador> indicadores = new HashMap<>();
	public static HashMap<Integer, Grupo> grupos = new HashMap<>();

	public static void initConfig() throws IOException {
		String configString = new String(Files.readAllBytes(Paths.get(URI_CONFIG)), CHARSET);
		JsonElement cfgFileElement = GSON.fromJson(configString, JsonElement.class);
		JsonObject cfgFileObj = cfgFileElement.getAsJsonObject();

		tipoServidor = cfgFileObj.get("server").getAsString();
		caminhoLog = cfgFileObj.get("logDirectory").getAsString();
		
		String[] emailSplit = cfgFileObj.get("email").getAsString().split(";");
		EMAIL_USERNAME = emailSplit[0];
		EMAIL_PASSWORD = emailSplit[1];
		
		String[] smsSplit = cfgFileObj.get("sms").getAsString().split(";");
		SMS_ACCOUNT_SID = smsSplit[0];
		SMS_AUTH_TOKEN = smsSplit[1];
		SMS_PHONE_NUMBER = smsSplit[2];

		JsonArray logPropertiesArray = cfgFileObj.get("logProperties").getAsJsonArray();
		JsonArray usersArray = cfgFileObj.get("users").getAsJsonArray();
		JsonArray indicatorsArray = cfgFileObj.get("indicators").getAsJsonArray();
		JsonArray groupsArray = cfgFileObj.get("groups").getAsJsonArray();

		Iterator<JsonElement> iterator = logPropertiesArray.iterator();
		while (iterator.hasNext()) {
			JsonObject obj = iterator.next().getAsJsonObject();
			FormatoLog formatoLog = new FormatoLog(obj.get(tipoServidor).getAsString(),
					InfoPropriedade.valueOf(obj.get("infoPropriedade").getAsString()),
					TipoValor.valueOf(obj.get("tipoValor").getAsString()));
			colunasLog.add(formatoLog);
		}
		
		iterator = usersArray.iterator();
		while (iterator.hasNext()) {
			JsonObject obj = iterator.next().getAsJsonObject();
			Usuario usuario = new Usuario(obj.get("id").getAsInt(), obj.get("nome").getAsString(),
					obj.get("telefone").getAsString(), obj.get("email").getAsString());
			usuarios.put(usuario.getId(), usuario);
		}

		iterator = indicatorsArray.iterator();
		while (iterator.hasNext()) {
			JsonObject obj = iterator.next().getAsJsonObject();
			JsonArray regrasArray = obj.get("regras").getAsJsonArray();
			Iterator<JsonElement> regraIterator = regrasArray.iterator();
			List<Regra> regras = new ArrayList<Regra>();
			while (regraIterator.hasNext()) {
				JsonObject regraObj = regraIterator.next().getAsJsonObject();
				Regra regra = new Regra(regraObj.get("descPropriedade").getAsString(),
						InfoPropriedade.valueOf(regraObj.get("infoPropriedade").getAsString()),
						TipoComparacao.valueOf(regraObj.get("tipoComparacao").getAsString()),
						TipoValor.valueOf(regraObj.get("tipoValor").getAsString()),
						regraObj.get("valor1").getAsString(),
						regraObj.get("valor2") != null ? regraObj.get("valor2").getAsString() : null);
				regras.add(regra);
			}

			Indicador indicador = new Indicador(obj.get("id").getAsInt(), obj.get("descricao").getAsString(), regras);
			indicadores.put(indicador.getId(), indicador);
		}

		iterator = groupsArray.iterator();
		while (iterator.hasNext()) {
			JsonObject obj = iterator.next().getAsJsonObject();

			JsonArray tmpArray = obj.get("users").getAsJsonArray();
			Iterator<JsonElement> tmpIterator = tmpArray.iterator();
			HashSet<Usuario> usersHashSet = new HashSet<>();
			while (tmpIterator.hasNext()) {
				JsonObject regraObj = tmpIterator.next().getAsJsonObject();
				usersHashSet.add(usuarios.get(regraObj.get("id").getAsInt()));
			}

			tmpArray = obj.get("indicators").getAsJsonArray();
			tmpIterator = tmpArray.iterator();
			HashSet<Indicador> indicatorsHashSet = new HashSet<>();
			while (tmpIterator.hasNext()) {
				JsonObject tmpObj = tmpIterator.next().getAsJsonObject();
				indicatorsHashSet.add(indicadores.get(tmpObj.get("id").getAsInt()));
			}

			Grupo grupo = new Grupo(obj.get("id").getAsInt(), obj.get("descricao").getAsString(), usersHashSet,
					indicatorsHashSet);
			grupos.put(grupo.getId(), grupo);
		}
	}
}
