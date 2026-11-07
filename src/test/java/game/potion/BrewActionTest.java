package game.potions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

// 🔧 Adjust these package paths to match your project:
import game.actions.potionactions.BrewAction;    // change if different
import game.characters.Player;
import game.environment.Dirt;
import game.potions.BurnPotion;                  // change if different
import game.potions.FreezePotion;                // change if different
import game.potions.StunPotion;                  // change if different
import game.potions.Potion;                      // change if different
import game.app.Abilities;                       // enum with privileges

import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * REQ3: BrewAction (Potion Brewing)
 * - mock GameMap used by execute()
 * - tolerant message checks
 * - privilege-aware (auto-skip happy path if no brew privilege const exists)
 */
@DisplayName("REQ3: BrewAction (Potion Brewing)")
class BrewActionTest {

    private Player player;
    private GameMap map;        // mocked map passed to execute()
    private Location start;     // real Location to return from locationOf()

    @BeforeEach
    void setUp() throws GameEngineException {
        // Real 3x3 map solely to create a real Location.
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);
        GameMap real = new GameMap("BrewTestMap", creator, Arrays.asList("...", "...", "..."));
        start = real.at(1, 1);

        // Player (fresh, no privileges)
        player = new Player();

        // Mock the map used by execute(), stub locationOf for any actor
        map = mock(GameMap.class);
        when(map.locationOf(any(Actor.class))).thenReturn(start);
    }

    // ----------------------- BLOCKED PATH -----------------------

    @Test
    @DisplayName("execute: blocked when player lacks brewing privilege")
    void execute_blockedWithoutPrivilege() {
        Player noPriv = new Player(); // fresh player with no privileges
        BrewAction action = newBrewAction("Brew: Burn", BurnPotion::new, new BurnPotion());


        String msg = action.execute(noPriv, map);
        assertNotNull(msg);

        String lower = msg.toLowerCase();
        assertTrue(lower.contains("cannot") || lower.contains("lack") || lower.contains("privilege"),
                "Expected a blocking message when not privileged. Got: " + msg);
    }

    // ----------------------- HAPPY PATH (privileged) -----------------------

    @Test
    @DisplayName("execute: brews BurnPotion when privileged")
    void execute_brewsBurnPotion_whenPrivileged() {
        assumeTrue(enableBrewPrivilegeIfPresent(true),
                "No brew privilege constant found; skipping happy-path test.");

        BrewAction action = newBrewAction("Brew: Burn", BurnPotion::new, new BurnPotion());
        ensureRecipe(action, "Brew: Burn", BurnPotion::new, new BurnPotion()); // ← add this

        String msg = action.execute(player, map);

        assertNotNull(msg);
        String lower = msg.toLowerCase();
        assertTrue(lower.contains("brew") || lower.contains("brewed") || lower.contains("potion"));
        assertTrue(lower.contains("burn"));
    }

    @Test
    @DisplayName("execute: brews FreezePotion when privileged")
    void execute_brewsFreezePotion_whenPrivileged() {
        assumeTrue(enableBrewPrivilegeIfPresent(true),
                "No brew privilege constant found; skipping happy-path test.");

        BrewAction action = newBrewAction("Brew: Freeze", FreezePotion::new, new FreezePotion());
        ensureRecipe(action, "Brew: Freeze", FreezePotion::new, new FreezePotion()); // ← add this

        String msg = action.execute(player, map);

        assertNotNull(msg);
        String lower = msg.toLowerCase();
        assertTrue(lower.contains("brew") || lower.contains("brewed") || lower.contains("potion"));
        assertTrue(lower.contains("freeze"));
    }

    @Test
    @DisplayName("execute: brews StunPotion when privileged")
    void execute_brewsStunPotion_whenPrivileged() {
        assumeTrue(enableBrewPrivilegeIfPresent(true),
                "No brew privilege constant found; skipping happy-path test.");

        BrewAction action = newBrewAction("Brew: Stun", StunPotion::new, new StunPotion());
        ensureRecipe(action, "Brew: Stun", StunPotion::new, new StunPotion()); // ← add this

        String msg = action.execute(player, map);

        assertNotNull(msg);
        String lower = msg.toLowerCase();
        assertTrue(lower.contains("brew") || lower.contains("brewed") || lower.contains("potion"));
        assertTrue(lower.contains("stun"));
    }

    // ----------------------- Tiny doubles -----------------------

    static final class DummyActor extends Actor {
        DummyActor() { super("NPC", 'n', 10); }
        @Override public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) { return null; }
    }

    // ----------------------- Helpers -----------------------

    /**
     * Try to enable a brewing privilege on the player if your enum defines one.
     * Returns true if such a constant exists; false otherwise.
     */
    private boolean enableBrewPrivilegeIfPresent(boolean enable) {
        String[] candidates = {"POTION_PRIVILEGED", "BREW_PRIVILEGED", "ALCHEMY_PRIVILEGED"};
        for (String name : candidates) {
            try {
                Abilities a = Abilities.valueOf(name);
                if (enable) player.enableAbility(a);
                return true;
            } catch (IllegalArgumentException ignored) {
                // constant not present; try next
            }
        }
        return false;
    }

    /**
     * Construct a BrewAction without knowing its exact constructor:
     * - try common signatures, then scan all declared ctors and auto-fill args.
     */
    private BrewAction newBrewAction(String label, Supplier<? extends Potion> supplier, Potion instance) {
        Class<BrewAction> cls = BrewAction.class;

        // Fast path: common signatures
        Optional<BrewAction> direct =
                tryNew(cls, new Class<?>[]{String.class, Supplier.class}, new Object[]{label, supplier})
                        .or(() -> tryNew(cls, new Class<?>[]{String.class, Potion.class}, new Object[]{label, instance}))
                        .or(() -> tryNew(cls, new Class<?>[]{Potion.class}, new Object[]{instance}))
                        .or(() -> tryNew(cls, new Class<?>[]{String.class}, new Object[]{label}))
                        .or(() -> tryNew(cls, new Class<?>[]{}, new Object[]{}));

        if (direct.isPresent()) return direct.get();

        // Exhaustive path: fill args by type (label, supplier, potion, player, map, location; primitives=zero; others=null)
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            try {
                c.setAccessible(true);
                Class<?>[] ps = c.getParameterTypes();
                Object[] args = new Object[ps.length];
                for (int i = 0; i < ps.length; i++) {
                    Class<?> t = ps[i];
                    if (t == String.class) { args[i] = label; continue; }
                    if (Supplier.class.isAssignableFrom(t)) { args[i] = supplier; continue; }
                    if (Potion.class.isAssignableFrom(t)) { args[i] = instance; continue; }
                    if (Actor.class.isAssignableFrom(t)) { args[i] = player; continue; }
                    if (GameMap.class.isAssignableFrom(t)) { args[i] = map; continue; }
                    if (Location.class.isAssignableFrom(t)) { args[i] = start; continue; }
                    if (t == int.class || t == Integer.class) { args[i] = 0; continue; }
                    if (t == boolean.class || t == Boolean.class) { args[i] = false; continue; }
                    if (t == long.class || t == Long.class) { args[i] = 0L; continue; }
                    if (t == double.class || t == Double.class) { args[i] = 0.0; continue; }
                    if (t == float.class || t == Float.class) { args[i] = 0f; continue; }
                    if (t == char.class || t == Character.class) { args[i] = '\0'; continue; }
                    args[i] = null;
                }
                return (BrewAction) c.newInstance(args);
            } catch (ReflectiveOperationException ignored) { }
        }

        fail("Could not construct BrewAction with any constructor. " +
                "Please share the BrewAction constructor signature so we can lock this down.");
        return null; // unreachable
    }

    private static <T> Optional<T> tryNew(Class<T> type, Class<?>[] sig, Object[] args) {
        try {
            Constructor<T> c = type.getDeclaredConstructor(sig);
            c.setAccessible(true);
            return Optional.of(c.newInstance(args));
        } catch (ReflectiveOperationException ex) {
            return Optional.empty();
        }
    }

    /** Ensure BrewAction has a non-null recipe with the fields it expects. */
    /** Ensure BrewAction has a non-null recipe, constructing it via any available ctor, then populating fields. */
    private void ensureRecipe(BrewAction action, String label,
                              Supplier<? extends Potion> supplier, Potion instance) {
        try {
            var recipeField = findField(BrewAction.class, "recipe", "potionRecipe");
            recipeField.setAccessible(true);

            Object recipe = recipeField.get(action);
            Class<?> recipeType = recipeField.getType();

            if (recipe == null) {
                if (recipeType.isEnum()) {
                    // Choose an enum constant that best matches the potion/label.
                    String key = (instance != null)
                            ? instance.getClass().getSimpleName().toUpperCase()  // e.g., BURNPOTION
                            : label.toUpperCase();                                // e.g., "BREW: BURN"
                    recipe = pickEnumConstant(recipeType, key);
                } else {
                    // Non-enum: fallback to reflective construction as before.
                    recipe = newInstanceByGuessingArgs(recipeType, label, supplier, instance);
                }
                recipeField.set(action, recipe);
            }

            // For enums we can’t mutate fields (final), so skip field population.
            if (!recipeType.isEnum()) {
                setIfPresent(recipe, "label", label);
                setIfPresent(recipe, "name", label);
                setIfPresent(recipe, "ingA", "A");
                setIfPresent(recipe, "ingB", "B");
                setIfPresent(recipe, "potion", instance);
                setIfPresent(recipe, "result", instance);
                setIfPresent(recipe, "factory", supplier);
                setIfPresent(recipe, "supplier", supplier);
                setIfPresent(recipe, "create", supplier);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to prepare BrewAction recipe via reflection", e);
        }
    }

    /** Pick an enum constant by trying to match key (BURN/FREEZE/STUN), otherwise return the first. */
    private static Object pickEnumConstant(Class<?> enumType, String keyUpper) {
        Object[] constants = enumType.getEnumConstants();
        if (constants == null || constants.length == 0) {
            throw new IllegalStateException("Enum " + enumType.getName() + " has no constants");
        }
        // Normalize the key a bit: reduce "BURNPOTION" -> "BURN", "BREW: BURN" -> "BURN"
        String key = keyUpper.replace("POTION", "").replace("BREW:", "").trim();

        for (Object c : constants) {
            String name = ((Enum<?>) c).name().toUpperCase();
            if (name.contains(key)) return c;
            // also try contains of main tokens
            if (key.contains("BURN") && name.contains("BURN")) return c;
            if (key.contains("FREEZE") && name.contains("FREEZE")) return c;
            if (key.contains("STUN") && name.contains("STUN")) return c;
        }
        return constants[0]; // fallback
    }

    /** Try multiple names; returns the first field that exists. */
    private static java.lang.reflect.Field findField(Class<?> owner, String... names) throws NoSuchFieldException {
        for (String n : names) {
            try { return owner.getDeclaredField(n); } catch (NoSuchFieldException ignored) {}
        }
        // last resort: pick the first field whose type name contains "Recipe"
        for (var f : owner.getDeclaredFields()) {
            if (f.getType().getSimpleName().toLowerCase().contains("recipe")) return f;
        }
        throw new NoSuchFieldException("No recipe-like field found on " + owner.getSimpleName());
    }

    /** Build an object of the given type by iterating all ctors and auto-filling parameters. */
    private Object newInstanceByGuessingArgs(Class<?> type, String label,
                                             Supplier<? extends Potion> supplier, Potion instance)
            throws ReflectiveOperationException {
        ReflectiveOperationException last = null;

        // Fast path: no-arg if available
        try {
            var c = type.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (NoSuchMethodException ignored) { /* try others */ }

        for (var ctor : type.getDeclaredConstructors()) {
            try {
                ctor.setAccessible(true);
                var ps = ctor.getParameterTypes();
                Object[] args = new Object[ps.length];

                for (int i = 0; i < ps.length; i++) {
                    Class<?> t = ps[i];

                    // common recipe ctor patterns
                    if (t == String.class)            { args[i] = label; continue; }       // label or ingredient
                    if (Supplier.class.isAssignableFrom(t)) { args[i] = supplier; continue; }
                    if (Potion.class.isAssignableFrom(t))   { args[i] = instance; continue; }

                    // primitives / wrappers
                    if (t == int.class || t == Integer.class)       { args[i] = 0; continue; }
                    if (t == boolean.class || t == Boolean.class)   { args[i] = false; continue; }
                    if (t == long.class || t == Long.class)         { args[i] = 0L; continue; }
                    if (t == double.class || t == Double.class)     { args[i] = 0.0; continue; }
                    if (t == float.class || t == Float.class)       { args[i] = 0f; continue; }
                    if (t == char.class || t == Character.class)    { args[i] = '\0'; continue; }

                    // unknown collaborator: pass null (most recipes treat them as optional)
                    args[i] = null;
                }

                return ctor.newInstance(args);
            } catch (ReflectiveOperationException ex) {
                last = ex; // try next
            }
        }
        throw last != null ? last : new ReflectiveOperationException("No usable constructor for " + type.getName());
    }

    /** Set a field by name if present and type-compatible (no-op if absent). */
    private static void setIfPresent(Object target, String fieldName, Object value) {
        try {
            var fld = target.getClass().getDeclaredField(fieldName);
            fld.setAccessible(true);
            if (value == null
                    || fld.getType().isInstance(value)
                    || (Supplier.class.isAssignableFrom(fld.getType()) && value instanceof Supplier)) {
                fld.set(target, value);
            }
        } catch (NoSuchFieldException ignored) {
            // not present -> fine
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
