package clip.core;

public enum ColorTier {
    NONE(0, null, null),      // No upgrade yet
    RED(100, ID.RED_PAPERCLIP, ID.RED_UPGRADE),
    GREEN(1_000, ID.GREEN_PAPERCLIP, ID.GREEN_UPGRADE),
    BLUE(5_000, ID.BLUE_PAPERCLIP, ID.BLUE_UPGRADE),
    PURPLE(10_000, ID.PURPLE_PAPERCLIP, ID.PURPLE_UPGRADE),
    YELLOW(50_000, ID.YELLOW_PAPERCLIP, ID.YELLOW_UPGRADE);

    private final int value;          // numeric value for save/load
    private final ID paperclipID;     // the type of paperclip unlocked at this tier
    private final ID upgradeID;       // the corresponding upgrade object

    ColorTier(int value, ID paperclipID, ID upgradeID) {
        this.value = value;
        this.paperclipID = paperclipID;
        this.upgradeID = upgradeID;
    }

    public int getValue() { return value; }
    public ID getPaperclipID() { return paperclipID; }
    public ID getUpgradeID() { return upgradeID; }

    // Get next tier in progression
    public ColorTier next() {
        int ordinal = this.ordinal();
        ColorTier[] values = ColorTier.values();
        if (ordinal < values.length - 1) return values[ordinal + 1];
        return null;
    }

    public static ColorTier fromValue(int value) {
        ColorTier result = NONE;
        for (ColorTier tier : ColorTier.values()) {
            if (value >= tier.value) result = tier;
        }
        return result;
    }
}
