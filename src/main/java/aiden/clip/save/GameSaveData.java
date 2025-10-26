package aiden.clip.save;

public class GameSaveData {
    public int clips;
    public int maxClipCount;
    public String coloredUpgrade; // store enum name
    public int valueUpgradeCount;
    public int moreUpgradeCount;

    public GameSaveData() {} // Needed for JSON deserialization

    public GameSaveData(int clips, int maxClipCount, String coloredUpgrade, int valueUpgradeCount, int moreUpgradeCount) {
        this.clips = clips;
        this.maxClipCount = maxClipCount;
        this.coloredUpgrade = coloredUpgrade;
        this.valueUpgradeCount = valueUpgradeCount;
        this.moreUpgradeCount = moreUpgradeCount;
    }
}
