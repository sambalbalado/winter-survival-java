package game.actions.playermorphactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.app.Abilities;
import game.characters.Player;
import game.characters.PlayerForm;
import game.weapons.Bite;
import game.weapons.Claw;

/**
 * Switches the {@link Player} between Explorer and animal forms (Wolf, Bear, Dragon).
 * <p>
 * This action is available only when the performer has {@link Abilities#MORPH_PRIVILEGED}.
 * Executing the action delegates to {@link Player#__setMorph(PlayerForm, IntrinsicWeapon)} to update
 * form state, abilities, and the player's intrinsic weapon in a single, consistent operation.
 * </p>
 *
 * <ul>
 *   <li><b>Form selection:</b> The target {@link PlayerForm} is inferred from a capability flag
 *   (e.g., {@link Abilities#FORM_WOLF} ⇒ {@link PlayerForm#WOLF}); {@code null} falls back to
 *   {@link PlayerForm#EXPLORER}.</li>
 *   <li><b>Intrinsic weapon:</b> When morphing <em>into</em> a form, the provided {@link IntrinsicWeapon}
 *   becomes the player's intrinsic for that form (e.g., {@link Bite}, {@link Claw}). Reverting to Explorer
 *   may pass {@code null} to restore the default.</li>
 *   <li><b>UX:</b> {@link #menuDescription(Actor)} prefixes the player's status line and a right arrow.</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see Abilities#MORPH_PRIVILEGED
 * @see Abilities#FORM_WOLF
 * @see Abilities#FORM_BEAR
 * @see Abilities#FORM_DRAGON
 * @see Player#__setMorph(PlayerForm, IntrinsicWeapon)
 * @see PlayerForm
 * @see IntrinsicWeapon
 */
public class MorphToAnimalAction extends Action {
    /**
     * Human-readable label shown in menus (e.g., "Morph: Wolf", "Revert to Explorer").
     */
    private final String label;

    /**
     * Capability that implies the target form (e.g., {@link Abilities#FORM_WOLF}).
     * <p>
     * May be {@code null} to indicate a revert to {@link PlayerForm#EXPLORER}.
     * </p>
     */
    private final Abilities formCap;

    /**
     * Intrinsic weapon to apply when switching <em>into</em> the target form.
     * <p>
     * May be {@code null} when reverting to Explorer.
     * </p>
     */
    private final IntrinsicWeapon intrinsic;

    /**
     * Creates a new morph action.
     *
     * @param label     menu label to display
     * @param formCap   capability that maps to the desired {@link PlayerForm}; {@code null} ⇒ Explorer
     * @param intrinsic intrinsic weapon to assign for the target form; may be {@code null} for Explorer
     */
    public MorphToAnimalAction(String label, Abilities formCap, IntrinsicWeapon intrinsic) {
        this.label = label;
        this.formCap = formCap;
        this.intrinsic = intrinsic;
    }

    /**
     * Executes the morph, provided the actor is a {@link Player} with {@link Abilities#MORPH_PRIVILEGED}.
     * <p>
     * The target form is derived from {@link #formCap} via {@link #toForm(Abilities)} and applied using
     * {@link Player#__setMorph(PlayerForm, IntrinsicWeapon)} which synchronizes abilities and intrinsic weapon.
     * </p>
     *
     * @param actor the performer; must be a {@link Player}
     * @param map   the current game map (unused directly, but part of the standard signature)
     * @return a descriptive message indicating the new form, or a failure reason
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Abilities.MORPH_PRIVILEGED)) {
            return actor + " cannot morph in this mode.";
        }
        if (!(actor instanceof Player p)) {
            return "Only the player can use this.";
        }

        // Decide the target form and apply it (this method already syncs abilities/flags)
        PlayerForm targetForm = toForm(formCap);
        p.__setMorph(targetForm, intrinsic);

        // Message
        if (targetForm == PlayerForm.EXPLORER) {
            return actor + " reverts to Explorer.";
        }
        return actor + " morphs into " + targetForm.name() + ".";
    }

    /**
     * {@inheritDoc}
     * <p>
     * Prepends the current player status line and a Unicode right arrow.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        // Prepend the full player status line
        return actor.toString() + " \u2192 " + label; // → arrow
    }

    /**
     * Maps a form capability to the corresponding {@link PlayerForm}.
     *
     * @param cap capability flag indicating the desired form
     * @return {@link PlayerForm#WOLF}, {@link PlayerForm#BEAR}, {@link PlayerForm#DRAGON}, or {@link PlayerForm#EXPLORER}
     */
    private static PlayerForm toForm(Abilities cap) {
        if (cap == Abilities.FORM_WOLF)   return PlayerForm.WOLF;
        if (cap == Abilities.FORM_BEAR)   return PlayerForm.BEAR;
        if (cap == Abilities.FORM_DRAGON) return PlayerForm.DRAGON;
        return PlayerForm.EXPLORER;
    }

    /**
     * Convenience helper that appends all common morph options to an {@link ActionList}.
     * <p>
     * By default:
     * </p>
     * <ul>
     *   <li>Wolf uses {@code new Bite(10, 5)}.</li>
     *   <li>Bear uses {@link Claw}.</li>
     *   <li>Dragon currently uses {@link Claw} as a temporary intrinsic.</li>
     *   <li>Explorer reverts with {@code null} intrinsic.</li>
     * </ul>
     *
     * @param actions the list to which morph options will be added
     */
    public static void addOptions(ActionList actions) {
        actions.add(new MorphToAnimalAction("Morph: Wolf",   Abilities.FORM_WOLF,   new Bite(10,5)));
        actions.add(new MorphToAnimalAction("Morph: Bear",   Abilities.FORM_BEAR,   new Claw()));
        actions.add(new MorphToAnimalAction("Morph: Dragon", Abilities.FORM_DRAGON, new Claw())); // temp intrinsic
        actions.add(new MorphToAnimalAction("Revert to Explorer", null, null));
    }

    /**
     * Determines the player's current form based on capabilities, using a stable priority:
     * <pre>DRAGON &gt; BEAR &gt; WOLF &gt; EXPLORER</pre>
     *
     * @param actor the actor to inspect
     * @return the inferred {@link PlayerForm} for the given actor
     */
    public static PlayerForm getCurrentForm(Actor actor) {
        if (actor instanceof game.characters.Player p) {
            // Check in a stable priority order (dragon > bear > wolf > explorer)
            if (p.hasAbility(game.app.Abilities.FORM_DRAGON)) return game.characters.PlayerForm.DRAGON;
            if (p.hasAbility(game.app.Abilities.FORM_BEAR))   return game.characters.PlayerForm.BEAR;
            if (p.hasAbility(game.app.Abilities.FORM_WOLF))   return game.characters.PlayerForm.WOLF;
        }
        return game.characters.PlayerForm.EXPLORER;
    }
}
