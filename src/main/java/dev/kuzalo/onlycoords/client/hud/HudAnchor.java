package dev.kuzalo.onlycoords.client.hud;

public enum HudAnchor {
	TOP_LEFT,
	TOP_CENTER,
	TOP_RIGHT,
	MIDDLE_LEFT,
	MIDDLE_CENTER,
	MIDDLE_RIGHT,
	BOTTOM_LEFT,
	BOTTOM_CENTER,
	BOTTOM_RIGHT;

	/**
	 * Computes the base position (top-left corner) of the content block within the container,
	 * according to the anchor, applying the offsets.
	 *
	 * @return an {@code [x, y]} array
	 */
	public int[] computePosition(int containerW, int containerH, int contentW, int contentH, int offsetX, int offsetY) {
		int x = switch (this) {
			case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> offsetX;
			case TOP_CENTER, MIDDLE_CENTER, BOTTOM_CENTER -> (containerW - contentW) / 2 + offsetX;
			case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> containerW - contentW - offsetX;
		};

		int y = switch (this) {
			case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> offsetY;
			case MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT -> (containerH - contentH) / 2 + offsetY;
			case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> containerH - contentH - offsetY;
		};

		return new int[] { x, y };
	}
}
