package dev.kuzalo.onlycoords.client.input;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*///?} else
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
//? if >=26.1
//import net.minecraft.resources.ResourceLocation;

import dev.kuzalo.onlycoords.client.OnlyCoordsClient;
import dev.kuzalo.onlycoords.client.config.ConfigManager;
import dev.kuzalo.onlycoords.client.config.CoordsConfig;

public final class KeyBindings {
	// 26.1 : catégorie = objet KeyMapping.Category.register(ResourceLocation) + KeyMappingHelper.
	// 1.21.8 : catégorie = String (clé de traduction) directement dans le constructeur + KeyBindingHelper.
	//? if >=26.1 {
	/*private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			ResourceLocation.fromNamespaceAndPath(OnlyCoordsClient.MOD_ID, "keys"));

	public static final KeyMapping TOGGLE = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.onlycoords.toggle", GLFW.GLFW_KEY_UNKNOWN, CATEGORY));
	*///?} else {
	public static final KeyMapping TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.onlycoords.toggle", GLFW.GLFW_KEY_UNKNOWN, "category.onlycoords.keys"));
	//?}

	private KeyBindings() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean toggled = false;
			while (TOGGLE.consumeClick()) {
				CoordsConfig config = ConfigManager.getInstance();
				config.enabled = !config.enabled;
				toggled = true;
			}
			if (toggled) {
				ConfigManager.save();
			}
		});
	}
}
