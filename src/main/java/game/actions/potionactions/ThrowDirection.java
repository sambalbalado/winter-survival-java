package game.actions.potionactions;

/**
 * Cardinal throw directions with their unit deltas and a user-friendly label.
 * <p>
 * Each constant stores:
 * </p>
 * <ul>
 *   <li><b>dx, dy:</b> Unit step applied on the X/Y axes (e.g., EAST = {@code (1, 0)}).</li>
 *   <li><b>pretty:</b> A human-readable name used in UI strings.</li>
 * </ul>
 *
 * <p>
 * Typical usage is to offset a position repeatedly by {@link #dx()} and {@link #dy()} when
 * simulating a projectile path in a straight line.
 * </p>
 *
 * @author Taiyo Arima
 * @version 1.0
 */
public enum ThrowDirection {
    /**
     * Upward on the grid: {@code (0, -1)}.
     */
    NORTH(0, -1, "North"),
    /**
     * Rightward on the grid: {@code (1, 0)}.
     */
    EAST(1, 0, "East"),
    /**
     * Downward on the grid: {@code (0, 1)}.
     */
    SOUTH(0, 1, "South"),
    /**
     * Leftward on the grid: {@code (-1, 0)}.
     */
    WEST(-1, 0, "West");

    /**
     * X-axis unit delta per step.
     */
    private final int dx;
    /**
     * Y-axis unit delta per step.
     */
    private final int dy;
    /**
     * Human-readable label for menus/logs.
     */
    private final String pretty;

    ThrowDirection(int dx, int dy, String pretty) {
        this.dx = dx;
        this.dy = dy;
        this.pretty = pretty;
    }

    /**
     * Returns the X-axis unit delta.
     */
    public int dx() { return dx; }

    /**
     * Returns the Y-axis unit delta.
     */
    public int dy() { return dy; }

    /**
     * Returns the human-readable direction label.
     */
    public String pretty() { return pretty; }
}
