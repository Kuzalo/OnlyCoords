package dev.kuzalo.onlycoords.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.kuzalo.onlycoords.client.OnlyCoordsClient;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(OnlyCoordsClient.MOD_ID);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("onlycoords.json");

	private static CoordsConfig instance;

	private ConfigManager() {
	}

	public static CoordsConfig getInstance() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public static CoordsConfig load() {
		if (!Files.exists(CONFIG_PATH)) {
			LOGGER.info("Aucune config trouvée, création de {} avec les valeurs par défaut", CONFIG_PATH.getFileName());
			CoordsConfig config = new CoordsConfig();
			save(config);
			instance = config;
			return config;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			CoordsConfig config = GSON.fromJson(reader, CoordsConfig.class);
			if (config == null) {
				throw new JsonParseException("fichier vide");
			}
			config.clamp();
			instance = config;
			return config;
		} catch (JsonParseException | IOException e) {
			LOGGER.warn("Config {} illisible ou corrompue ({}), utilisation des valeurs par défaut",
					CONFIG_PATH.getFileName(), e.getMessage());
			CoordsConfig config = new CoordsConfig();
			instance = config;
			return config;
		}
	}

	// Sauvegarde l'instance courante. Sert de Runnable pour le bouton Save de l'écran YACL.
	public static void save() {
		save(getInstance());
	}

	public static void save(CoordsConfig config) {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException e) {
			LOGGER.warn("Échec de la sauvegarde de la config {} : {}", CONFIG_PATH.getFileName(), e.getMessage());
		}
	}
}
