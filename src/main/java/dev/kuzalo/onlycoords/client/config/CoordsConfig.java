package dev.kuzalo.onlycoords.client.config;

import dev.kuzalo.onlycoords.client.hud.HudAnchor;

public class CoordsConfig {
	public boolean enabled = true;
	public boolean showDecimals = false;
	public int decimalCount = 1;
	public float scale = 1.0f;
	public HudAnchor anchor = HudAnchor.TOP_LEFT;
	public int offsetX = 4;
	public int offsetY = 4;
	public int textColor = 0xFFFFFFFF;
	public boolean dropShadow = true;
	public boolean background = false;
	public int backgroundColor = 0x80000000;
	public boolean showDirection = false;
	public boolean hideWhenF3Open = true;

	// The JSON can be hand-edited: clamp the bounded fields back into their valid range after reading.
	public void clamp() {
		if (anchor == null) {
			anchor = HudAnchor.TOP_LEFT;
		}
		decimalCount = Math.max(0, Math.min(3, decimalCount));
		scale = Math.max(0.5f, Math.min(2.0f, scale));
	}
}
