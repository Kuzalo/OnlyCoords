package dev.kuzalo.onlycoords.client.input;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*///?} else
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
// Key category (1.21.9+) is registered with a resource id: ResourceLocation, renamed Identifier at 1.21.11.
//? if >=1.21.11
//import net.minecraft.resources.Identifier;
//? if >=1.21.9 && <1.21.11
//import net.minecraft.resources.ResourceLocation;

import dev.kuzalo.onlycoords.client.OnlyCoordsClient;
import dev.kuzalo.onlycoords.client.config.ConfigManager;
import dev.kuzalo.onlycoords.client.config.CoordsConfig;

public final class KeyBindings {
	// Key category type changed at MC 1.21.9: a KeyMapping.Category object (registered with a resource
	// id) instead of a String translation key. The resource id class is ResourceLocation, renamed to
	// Identifier at 1.21.11. Hence three tiers: String (<1.21.9), Category+ResourceLocation
	// (1.21.9-1.21.10) and Category+Identifier (>=1.21.11, incl. 26.1.x).
	//? if >=1.21.11 {
	/*private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(OnlyCoordsClient.MOD_ID, "keys"));
	private static final KeyMapping TOGGLE_KEY = new KeyMapping(
			"key.onlycoords.toggle", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
	*///?}
	//? if >=1.21.9 && <1.21.11 {
	/*private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			ResourceLocation.fromNamespaceAndPath(OnlyCoordsClient.MOD_ID, "keys"));
	private static final KeyMapping TOGGLE_KEY = new KeyMapping(
			"key.onlycoords.toggle", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
	*///?}
	//? if <1.21.9 {
	private static final KeyMapping TOGGLE_KEY = new KeyMapping(
			"key.onlycoords.toggle", GLFW.GLFW_KEY_UNKNOWN, "category.onlycoords.keys");
	//?}

	// Registration helper changed at MC 26.1: KeyMappingHelper instead of KeyBindingHelper.
	//? if >=26.1 {
	/*public static final KeyMapping TOGGLE = KeyMappingHelper.registerKeyMapping(TOGGLE_KEY);
	*///?} else {
	public static final KeyMapping TOGGLE = KeyBindingHelper.registerKeyBinding(TOGGLE_KEY);
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
