package aiden.clip.core;

public enum ColorTier {
    NONE(null, null),       // No upgrade yet
    RED(ID.RED_PAPERCLIP, ID.RED_UPGRADE),
    GREEN(ID.GREEN_PAPERCLIP, ID.GREEN_UPGRADE),
    BLUE(ID.BLUE_PAPERCLIP, ID.BLUE_UPGRADE),
    PURPLE(ID.PURPLE_PAPERCLIP, ID.PURPLE_UPGRADE),
    YELLOW(ID.YELLOW_PAPERCLIP, ID.YELLOW_UPGRADE);

    private final ID paperclipID; // The type of paperclip unlocked at this tier
    private final ID upgradeID;   // The corresponding upgrade object in the HUD

    ColorTier(ID paperclipID, ID upgradeID) {
        this.paperclipID = paperclipID;
        this.upgradeID = upgradeID;
    }

    public ID getPaperclipID() { return paperclipID; }
    public ID getUpgradeID() { return upgradeID; }

    // Get the next tier in progression
    public ColorTier next() {
        int ordinal = this.ordinal();
        ColorTier[] values = ColorTier.values();
        if (ordinal < values.length - 1) return values[ordinal + 1];
        return null;
    }

    // Optional: get previous tier if needed
    public ColorTier previous() {
        int ordinal = this.ordinal();
        if (ordinal > 0) return ColorTier.values()[ordinal - 1];
        return null;
    }
}
