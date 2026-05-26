package dev.kuzalo.onlycoords.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.kuzalo.onlycoords.client.OnlyCoordsClient;
import dev.kuzalo.onlycoords.client.config.ConfigManager;
import dev.kuzalo.onlycoords.client.config.CoordsConfig;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
// ResourceLocation was renamed to Identifier at MC 1.21.11 (26.1.x keeps Identifier).
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else
import net.minecraft.resources.ResourceLocation;

public class CoordsHudRenderer implements HudElement {
	//? if >=1.21.11 {
	/*private static final Identifier ID = Identifier.fromNamespaceAndPath(OnlyCoordsClient.MOD_ID, "coords_hud");
	*///?} else {
	private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnlyCoordsClient.MOD_ID, "coords_hud");
	//?}
	private static final int LINE_GAP = 2;
	private static final String[] CARDINALS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

	public static void register() {
		HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, ID, new CoordsHudRenderer());
	}

	// 26.1: extractRenderState(GuiGraphics, DeltaTracker) — "extract render state" model.
	// 1.21.8: render(GuiGraphics, DeltaTracker). The business logic is shared in renderHud(...).
	//? if >=26.1 {
	/*@Override
	public void extractRenderState(GuiGraphics graphics, DeltaTracker tickCounter) {
		renderHud(graphics);
	}
	*///?} else {
	@Override
	public void render(GuiGraphics graphics, DeltaTracker tickCounter) {
		renderHud(graphics);
	}
	//?}

	private void renderHud(GuiGraphics graphics) {
		CoordsConfig config = ConfigManager.getInstance();
		if (!config.enabled) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) {
			return;
		}

		if (config.hideWhenF3Open && client.getDebugOverlay().showDebugScreen()) {
			return;
		}

		double x = client.player.getX();
		double y = client.player.getY();
		double z = client.player.getZ();

		List<String> lines = new ArrayList<>(2);
		lines.add(formatCoords(config, x, y, z));
		if (config.showDirection) {
			lines.add("Facing: " + cardinal(client.player.getYRot()));
		}

		Font font = client.font;
		int lineHeight = font.lineHeight;

		int textWidth = 0;
		for (String line : lines) {
			textWidth = Math.max(textWidth, font.width(line));
		}
		int textHeight = lineHeight * lines.size() + LINE_GAP * (lines.size() - 1);

		int scaledTextWidth = Math.round(textWidth * config.scale);
		int scaledTextHeight = Math.round(textHeight * config.scale);

		int containerW = graphics.guiWidth();
		int containerH = graphics.guiHeight();

		int[] pos = config.anchor.computePosition(containerW, containerH, scaledTextWidth, scaledTextHeight, config.offsetX, config.offsetY);
		int posX = pos[0];
		int posY = pos[1];

		if (config.background) {
			graphics.fill(posX - 2, posY - 2, posX + scaledTextWidth + 2, posY + scaledTextHeight + 2, config.backgroundColor);
		}

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) posX, (float) posY);
		matrices.scale(config.scale, config.scale);

		int lineY = 0;
		for (String line : lines) {
			graphics.drawString(font, line, 0, lineY, config.textColor, config.dropShadow);
			lineY += lineHeight + LINE_GAP;
		}

		matrices.popMatrix();
	}

	private static String formatCoords(CoordsConfig config, double x, double y, double z) {
		if (!config.showDecimals) {
			// floor (not a direct cast) otherwise negative coords are wrong: (int) -0.5 == 0 instead of -1.
			return "X: " + (int) Math.floor(x) + " Y: " + (int) Math.floor(y) + " Z: " + (int) Math.floor(z);
		}

		String fmt = "X: %." + config.decimalCount + "f Y: %." + config.decimalCount + "f Z: %." + config.decimalCount + "f";
		// Locale.ROOT to force the decimal point (not the comma in FR locale).
		return String.format(Locale.ROOT, fmt, x, y, z);
	}

	private static String cardinal(float yaw) {
		float normalized = ((yaw % 360.0f) + 360.0f) % 360.0f;
		int sector = Math.round(normalized / 45.0f) % 8;
		return CARDINALS[sector];
	}
}
