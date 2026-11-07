package game.status;

/**
 * Strategy/enum for the source of a burn.
 * Each constant supplies its own defaults and label.
 * This avoids instanceof checks in client code.
 */
public enum BurnSource {
    TORCH("Torch", 7, 3),
    FireTile("FireTile", 5, 5),
    DRAGON("Dragon", 6, 6);


    private final String label;
    private final int defaultDuration;
    private final int defaultDamagePerTurn;

    BurnSource(String label, int defaultDuration, int defaultDamagePerTurn) {
        this.label = label;
        this.defaultDuration = defaultDuration;
        this.defaultDamagePerTurn = defaultDamagePerTurn;
    }

    public String label() { return label; }
    public int defaultDuration() { return defaultDuration; }
    public int defaultDamagePerTurn() { return defaultDamagePerTurn; }
}
