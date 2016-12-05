package net.wouto.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.wouto.proxy.cache.GameProfileCache;
import net.wouto.proxy.exception.InvalidProxyKeyException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@SpringBootApplication
public class MojangProxyServer {

	public static boolean LOG_UNKNOWN_REQUESTS = false;
	public static boolean LOG_KNOWN_REQUESTS = true;
	public static String GAME_PROFILE_CACHE_CLASS = null;
	public static String AUTH_KEY = null;

	private static MojangProxyServer instance;
	private ObjectMapper objectMapper;
	private ObjectMapper objectMapperPretty;
	private GameProfileCache gameProfileCache;

	private static Config config;

	public void start(String[] args) {
 		this.objectMapper = new ObjectMapper();
		this.objectMapperPretty = new ObjectMapper();
		this.objectMapperPretty.configure(SerializationFeature.INDENT_OUTPUT, true);
		if (GAME_PROFILE_CACHE_CLASS == null) {
			this.gameProfileCache = new GameProfileCache(MojangProxyServer.config);
			System.out.println("Using default in-memory GameProfileCache");
		} else {
			try {
				Class<GameProfileCache> c = (Class<GameProfileCache>) Class.forName(GAME_PROFILE_CACHE_CLASS);
				Constructor<GameProfileCache> cons = c.getDeclaredConstructor(Config.class);
				this.gameProfileCache = cons.newInstance(MojangProxyServer.config);
			} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Config c = Config.getConfig(new File("proxy.properties"));
		config = c;

		LOG_UNKNOWN_REQUESTS = Boolean.parseBoolean(c.getProperty("logAllUnknownRequests", "false"));
		LOG_KNOWN_REQUESTS = Boolean.parseBoolean(c.getProperty("logKnownRequests", "true"));
		GAME_PROFILE_CACHE_CLASS = c.getProperty("gameProfileCacheClass", null);
		AUTH_KEY = c.getProperty("authKey", null);

		if (GAME_PROFILE_CACHE_CLASS != null && GAME_PROFILE_CACHE_CLASS.isEmpty()) {
			GAME_PROFILE_CACHE_CLASS = null;
		}

		String host = c.getProperty("hostname", "0.0.0.0");
		int port = Integer.parseInt(c.getProperty("port", "8000"));

		HashMap<String, Object> props = new HashMap<>();
		props.put("server.address", host);
		props.put("server.port", port);

		instance = new MojangProxyServer();
		instance.start(args);
		new SpringApplicationBuilder()
				.sources(MojangProxyServer.class)
				.properties(props)
				.run(args);
	}

	public static void authorize(String key) throws InvalidProxyKeyException {
		if (AUTH_KEY == null) {
			return;
		}
		if (key.equals(AUTH_KEY)) {
			return;
		}
		throw new InvalidProxyKeyException();
	}

	public GameProfileCache getGameProfileCache() {
		return gameProfileCache;
	}

	public static MojangProxyServer get() {
		return instance;
	}

	public ObjectMapper getMapper(boolean pretty) {
		return (pretty ? this.objectMapperPretty : this.objectMapper);
	}

}
