package game.creatures;

import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.app.Abilities;
import game.weapons.CrocodileBite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Crocodile} (REQ2: New Animal).
 *
 * <p>Covers:</p>
 * <ul>
 *   <li>Constructor invariants (name, display, HP, warmth)</li>
 *   <li>Abilities (PREDATOR)</li>
 *   <li>Intrinsic weapon type & stats</li>
 *   <li>Warmth boundaries and clamping</li>
 * </ul>
 */
class CrocodileTest {

    // ========================================================================
    // 1) Constructor invariants
    // ========================================================================

    @Test @DisplayName("Name is 'Crocodile'")
    void name_isCrocodile() {
        Crocodile c = new Crocodile();
        assertTrue(c.toString().startsWith("Crocodile"),
                "Expected toString() to start with 'Crocodile' but was: " + c.toString());
    }

    @Test @DisplayName("Display char is '<'")
    void displayChar_isLt() {
        Crocodile c = new Crocodile();
        assertEquals('<', c.getDisplayChar());
    }

    @Test @DisplayName("Starts with 300 health")
    void health_is300() {
        Crocodile c = new Crocodile();
        assertEquals(300, c.getAttribute(BaseAttributes.HEALTH));
    }

    @Test @DisplayName("Warmth starts 55 then Animal ctor decrements to 54")
    void warmth_is54AfterCtor() {
        Crocodile c = new Crocodile();
        assertEquals(54, c.getAttribute(AnimalAttributes.WARMTH));
    }

    // ========================================================================
    // 2) Abilities
    // ========================================================================

    @Test @DisplayName("Has PREDATOR ability")
    void has_predatorAbility() {
        Crocodile c = new Crocodile();
        assertTrue(c.hasAbility(Abilities.PREDATOR));
    }

    // ========================================================================
    // 3) Intrinsic weapon
    // ========================================================================

    @Test @DisplayName("Has non-null intrinsic weapon")
    void intrinsic_notNull() {
        Crocodile c = new Crocodile();
        assertNotNull(c.getIntrinsicWeapon());
    }

    @Test @DisplayName("Intrinsic weapon is CrocodileBite")
    void intrinsic_isCrocodileBite() {
        Crocodile c = new Crocodile();
        assertInstanceOf(CrocodileBite.class, c.getIntrinsicWeapon());
    }

    @Test @DisplayName("CrocodileBite stats: damage=80, hitRate=75, verb contains 'bite'")
    void bite_statsCorrect() {
        Crocodile c = new Crocodile();
        IntrinsicWeapon w = c.getIntrinsicWeapon();

        int damage = getIntField(w, "damage");
        int hitRate = getIntField(w, "hitRate");
        String verb = getStringField(w, "verb");

        assertEquals(80, damage, "damage should be 80");
        assertEquals(75, hitRate, "hitRate should be 75");
        assertTrue(hitRate >= 0 && hitRate <= 100, "hitRate in [0,100]");
        assertNotNull(verb);
        assertFalse(verb.isBlank());
        assertTrue(verb.toLowerCase().contains("bite"), "verb should contain 'bite'");
    }

    @Test @DisplayName("getIntrinsicWeapon returns a fresh instance with consistent stats")
    void intrinsic_isNewObjectEachTime() {
        Crocodile c = new Crocodile();
        IntrinsicWeapon w1 = c.getIntrinsicWeapon();
        IntrinsicWeapon w2 = c.getIntrinsicWeapon();

        assertNotSame(w1, w2, "should return new instance each time");
        assertEquals(getIntField(w1, "damage"),  getIntField(w2, "damage"));
        assertEquals(getIntField(w1, "hitRate"), getIntField(w2, "hitRate"));
        assertEquals(getStringField(w1, "verb"), getStringField(w2, "verb"));
    }

    // ========================================================================
    // 4) Warmth boundaries
    // ========================================================================

    @Test @DisplayName("Conscious at spawn")
    void conscious_atSpawn() {
        Crocodile c = new Crocodile();
        assertTrue(c.isConscious());
    }

    @Test @DisplayName("Warmth can be reduced to exactly zero")
    void warmth_toZero() {
        Crocodile c = new Crocodile();
        int current = c.getAttribute(AnimalAttributes.WARMTH);
        c.modifyAttribute(AnimalAttributes.WARMTH, ActorAttributeOperation.DECREASE, current);
        assertEquals(0, c.getAttribute(AnimalAttributes.WARMTH));
        // If spec says 0 => unconscious, uncomment:
        // assertFalse(c.isConscious());
    }

    @Test @DisplayName("Warmth clamps at zero (no underflow)")
    void warmth_clampsAtZero() {
        Crocodile c = new Crocodile();
        c.modifyAttribute(AnimalAttributes.WARMTH, ActorAttributeOperation.DECREASE, 1_000);
        assertEquals(0, c.getAttribute(AnimalAttributes.WARMTH));
    }

    @Test @DisplayName("Warmth 1: still conscious")
    void warmth_oneStillConscious() {
        Crocodile c = new Crocodile();
        int current = c.getAttribute(AnimalAttributes.WARMTH);
        c.modifyAttribute(AnimalAttributes.WARMTH, ActorAttributeOperation.DECREASE, current - 1);
        assertEquals(1, c.getAttribute(AnimalAttributes.WARMTH));
        assertTrue(c.isConscious());
    }

    // ========================================================================
    // Helpers: private/protected field access
    // ========================================================================

    private static int getIntField(Object obj, String name) {
        try {
            Field f = getField(obj.getClass(), name);
            f.setAccessible(true);
            Object v = f.get(obj);
            return (v instanceof Number n) ? n.intValue() : (Integer) v;
        } catch (Exception e) {
            fail("Cannot read int field '" + name + "' from " + obj.getClass().getName() + ": " + e);
            return -1;
        }
    }

    private static String getStringField(Object obj, String name) {
        try {
            Field f = getField(obj.getClass(), name);
            f.setAccessible(true);
            Object v = f.get(obj);
            return (v == null) ? null : v.toString();
        } catch (Exception e) {
            fail("Cannot read String field '" + name + "' from " + obj.getClass().getName() + ": " + e);
            return null;
        }
    }

    /** Walks class hierarchy to find the field (covers subclass/superclass). */
    private static Field getField(Class<?> cls, String name) throws NoSuchFieldException {
        Class<?> cur = cls;
        while (cur != null) {
            try { return cur.getDeclaredField(name); }
            catch (NoSuchFieldException ignored) { cur = cur.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }
}
