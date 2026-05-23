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
			LOGGER.info("No config found, creating {} with default values", CONFIG_PATH.getFileName());
			CoordsConfig config = new CoordsConfig();
			save(config);
			instance = config;
			return config;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			CoordsConfig config = GSON.fromJson(reader, CoordsConfig.class);
			if (config == null) {
				throw new JsonParseException("empty file");
			}
			config.clamp();
			instance = config;
			return config;
		} catch (JsonParseException | IOException e) {
			LOGGER.warn("Config {} unreadable or corrupted ({}), using default values",
					CONFIG_PATH.getFileName(), e.getMessage());
			CoordsConfig config = new CoordsConfig();
			instance = config;
			return config;
		}
	}

	// Saves the current instance. Used as a Runnable for the YACL screen's Save button.
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
			LOGGER.warn("Failed to save config {}: {}", CONFIG_PATH.getFileName(), e.getMessage());
		}
	}
}
