package org.nerif.util;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.nerif.gson.Gson;
import org.nerif.gson.JsonElement;
import org.nerif.gson.JsonObject;

public class Config {
	public static final Gson GSON = new Gson();
	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static final Random RANDOM = new Random(System.nanoTime());
	
	public static void initConfig() {
    	
    	/*String configString = new String(Files.readAllBytes(Paths.get(URI_CONFIG)), CHARSET);
    	JsonElement cfgFileElement = GSON.fromJson(configString, JsonElement.class);
    	JsonObject cfgFileObj = cfgFileElement.getAsJsonObject();
    	
    	JsonObject cfgObj = cfgFileObj.get("shaolinbot").getAsJsonObject().get("config").getAsJsonObject();*/
	}
}
