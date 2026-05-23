package dev.kuzalo.onlycoords.client;

import dev.kuzalo.onlycoords.client.config.ConfigManager;
import dev.kuzalo.onlycoords.client.config.CoordsConfig;
import dev.kuzalo.onlycoords.client.hud.CoordsHudRenderer;
import dev.kuzalo.onlycoords.client.input.KeyBindings;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlyCoordsClient implements ClientModInitializer {
	public static final String MOD_ID = "onlycoords";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// ModMenuIntegration is loaded by ModMenu via the "modmenu" entrypoint.
		CoordsConfig config = ConfigManager.load();
		CoordsHudRenderer.register();
		KeyBindings.register();
		LOGGER.info("OnlyCoords initialized (HUD {})", config.enabled ? "enabled" : "disabled");
	}
}
