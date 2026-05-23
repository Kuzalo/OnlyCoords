package dev.kuzalo.onlycoords.client.config;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;

import dev.kuzalo.onlycoords.client.hud.HudAnchor;

// 26.1 : Mojang mappings — Text -> Component (net.minecraft.network.chat), Screen dans le package "screens".
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
	// Valeurs par défaut pour le bouton "reset" de chaque option (instance figée, distincte du singleton live).
	private static final CoordsConfig DEFAULTS = new CoordsConfig();

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> buildYaclScreen(parent);
	}

	private static Screen buildYaclScreen(Screen parent) {
		return YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("text.config.onlycoords.title"))
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("category.onlycoords.general"))
						.option(boolOption("enabled", DEFAULTS.enabled,
								() -> cfg().enabled, v -> cfg().enabled = v, true))
						.option(boolOption("hideWhenF3Open", DEFAULTS.hideWhenF3Open,
								() -> cfg().hideWhenF3Open, v -> cfg().hideWhenF3Open = v, false))
						.option(boolOption("showDirection", DEFAULTS.showDirection,
								() -> cfg().showDirection, v -> cfg().showDirection = v, false))
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("category.onlycoords.format"))
						.option(boolOption("showDecimals", DEFAULTS.showDecimals,
								() -> cfg().showDecimals, v -> cfg().showDecimals = v, false))
						.option(intFieldOption("decimalCount", DEFAULTS.decimalCount,
								() -> cfg().decimalCount, v -> cfg().decimalCount = v, 0, 3))
						.option(floatSliderOption("scale", DEFAULTS.scale,
								() -> cfg().scale, v -> cfg().scale = v, 0.5f, 2.0f, 0.1f))
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("category.onlycoords.position"))
						.option(enumOption("anchor", DEFAULTS.anchor,
								() -> cfg().anchor, v -> cfg().anchor = v, HudAnchor.class))
						.option(intSliderOption("offsetX", DEFAULTS.offsetX,
								() -> cfg().offsetX, v -> cfg().offsetX = v, -200, 200, 1))
						.option(intSliderOption("offsetY", DEFAULTS.offsetY,
								() -> cfg().offsetY, v -> cfg().offsetY = v, -200, 200, 1))
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("category.onlycoords.appearance"))
						.option(colorOption("textColor", DEFAULTS.textColor,
								() -> cfg().textColor, v -> cfg().textColor = v))
						.option(boolOption("dropShadow", DEFAULTS.dropShadow,
								() -> cfg().dropShadow, v -> cfg().dropShadow = v, false))
						.option(boolOption("background", DEFAULTS.background,
								() -> cfg().background, v -> cfg().background = v, false))
						.option(colorOption("backgroundColor", DEFAULTS.backgroundColor,
								() -> cfg().backgroundColor, v -> cfg().backgroundColor = v))
						.build())
				// Sauvegarde uniquement au clic sur "Save" (les setters ne font qu'assigner le singleton).
				.save(ConfigManager::save)
				.build()
				.generateScreen(parent);
	}

	private static CoordsConfig cfg() {
		return ConfigManager.getInstance();
	}

	private static Component name(String key) {
		return Component.translatable("option.onlycoords." + key);
	}

	private static OptionDescription desc(String key) {
		return OptionDescription.of(Component.translatable("option.onlycoords." + key + ".description"));
	}

	private static Option<Boolean> boolOption(String key, boolean def, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean onOff) {
		return Option.<Boolean>createBuilder()
				.name(name(key))
				.description(desc(key))
				.binding(Binding.generic(def, getter, setter))
				.controller(opt -> onOff
						? BooleanControllerBuilder.create(opt).onOffFormatter()
						: BooleanControllerBuilder.create(opt))
				.build();
	}

	private static Option<Integer> intFieldOption(String key, int def, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max) {
		return Option.<Integer>createBuilder()
				.name(name(key))
				.description(desc(key))
				.binding(Binding.generic(def, getter, setter))
				.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(min, max))
				.build();
	}

	private static Option<Integer> intSliderOption(String key, int def, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int step) {
		return Option.<Integer>createBuilder()
				.name(name(key))
				.description(desc(key))
				.binding(Binding.generic(def, getter, setter))
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(step))
				.build();
	}

	private static Option<Float> floatSliderOption(String key, float def, Supplier<Float> getter, Consumer<Float> setter, float min, float max, float step) {
		return Option.<Float>createBuilder()
				.name(name(key))
				.description(desc(key))
				.binding(Binding.generic(def, getter, setter))
				.controller(opt -> FloatSliderControllerBuilder.create(opt).range(min, max).step(step))
				.build();
	}

	private static <T extends Enum<T>> Option<T> enumOption(String key, T def, Supplier<T> getter, Consumer<T> setter, Class<T> enumClass) {
		return Option.<T>createBuilder()
				.name(name(key))
				.description(desc(key))
				.binding(Binding.generic(def, getter, setter))
				.controller(opt -> EnumControllerBuilder.create(opt).enumClass(enumClass))
				.build();
	}

	// textColor / backgroundColor sont des int ARGB ; YACL ColorController travaille avec java.awt.Color
	// (inchangé en 3.9.x) → conversion int <-> Color au binding.
	private static Option<Color> colorOption(String key, int defArgb, IntSupplier getter, IntConsumer setter) {
		return Option.<Color>createBuilder()
				.name(name(key))
				.description(desc(key))
				.binding(Binding.generic(
						new Color(defArgb, true),
						() -> new Color(getter.getAsInt(), true),
						c -> setter.accept(c.getRGB())))
				.controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
				.build();
	}
}
