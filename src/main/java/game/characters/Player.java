package game.characters;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import game.actions.AttackAction;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseActorAttribute;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Menu;
import game.api.LunarPhaseManager;
import game.actions.playermorphactions.MorphToAnimalAction;
import game.actions.playermorphactions.ElementalBreathAction;
import game.actions.playermorphactions.FlightMoveAction;
import game.actions.playermorphactions.ShakeTreeAction;
import game.actions.playermorphactions.SmellTreasureAction;
import game.actions.potionactions.BoostedMoveAction;
import game.items.*;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.CoatWeaponAction;
import game.app.Abilities;
import game.capabilities.CoatableWeapon;
import game.capabilities.WeaponWithActions;
import game.actions.SleepAction;
import game.status.SpeedBoostStatus;
import game.weapons.Axe;
import game.weapons.BareFist;
import game.weapons.Bow;
import game.weapons.Torch;
import game.status.Ignite;
import game.status.BurnSource;
import game.app.GameRules;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The main player character with survival mechanics.
 * The Explorer has hydration and warmth stats that decrease over time.
 * If both stats reach zero for consecutive turns, the explorer dies.
 * The explorer can drink, consume items, tame animals, and sleep on bedrolls.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class Player extends Actor implements Drinkable, Sleepable {

    private boolean sleeping = false;
    private boolean wasAtZeroLastTurn = false;
    private Set<WeaponWithActions> weapons = new HashSet<>();
    private Set<CoatableWeapon> coatableWeapons = new HashSet<>();


    /**
     * Enumeration of Explorer-specific attributes.
     */
    public enum ExplorerAttributes {
        HYDRATION, WARMTH
    }

    /**
     * Creates a new Explorer with initial hydration and warmth values.
     */
    public Player() {
        super("Explorer", 'ඞ', 1000);

        this.addNewStatistic(ExplorerAttributes.HYDRATION, new BaseActorAttribute(1000));
        this.addNewStatistic(ExplorerAttributes.WARMTH, new BaseActorAttribute(1000));
        this.enableAbility(Abilities.CAN_DRINK);
        this.enableAbility(Abilities.CAN_CONSUME);
        this.enableAbility(Abilities.CAN_TAME);

        Axe axe = new Axe();
        Bow bow = new Bow();
        Torch torch = new Torch();

        weapons.add(axe);
        weapons.add(bow);
        weapons.add(torch);

        coatableWeapons.add(axe);
        coatableWeapons.add(bow);

        this.addItemToInventory(new YewBerry());

        this.addItemToInventory(new Apple());
    }



    /**
     * Returns the explorer's intrinsic weapon.
     *
     * @return a BareFist weapon for unarmed combat
     */
    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return Objects.requireNonNullElseGet(__morphedIntrinsic, BareFist::new);
    }

    /**
     * Sets the sleeping state of the explorer.
     *
     * @param sleeping true if the explorer is sleeping, false otherwise
     */
    @Override
    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    /**
     * Handles the explorer's turn logic including survival mechanics.
     *
     * Performs the following operations each turn:
     * - Apply lunar effect changes
     * - Decreases hydration and warmth
     * - Checks for fire damage and applies burning status
     * - Allows weapon coating on snow terrain
     * - Processes burning damage
     * - Checks death conditions (both stats at zero for two consecutive turns)
     * - Adds available actions (sleep, attack, ranged attacks)
     *
     * @param actions    available actions for this turn
     * @param lastAction the action performed last turn
     * @param map        the current game map
     * @param display    the display for output
     * @return the action to perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        game.status.SpeedBoostStatus.tick(this);
        final boolean creative = hasAbility(Abilities.CREATIVE_MODE);
        if (creative) {
            try { MorphToAnimalAction.addOptions(actions); } catch (Throwable __ignore) {}
        }


        // ── 0) Inline Creative Mode toggle action (no new classes)
        actions.add(new Action() {
            @Override
            public String execute(Actor actor, GameMap m) {
                if (actor.hasAbility(Abilities.CREATIVE_MODE)) {
                    actor.disableAbility(Abilities.CREATIVE_MODE);
                    actor.disableAbility(Abilities.MORPH_PRIVILEGED);
                    actor.disableAbility(Abilities.POTION_PRIVILEGED);
                    // Optional: force revert when turning Creative OFF
                    if (__getMorph() != PlayerForm.EXPLORER) __setMorph(PlayerForm.EXPLORER, null);
                    return "Creative Mode disabled.";
                } else {
                    actor.enableAbility(Abilities.CREATIVE_MODE);
                    actor.enableAbility(Abilities.MORPH_PRIVILEGED);
                    actor.enableAbility(Abilities.POTION_PRIVILEGED);
                    return "Creative Mode enabled. Morph & potion privileges granted.";
                }
            }
            @Override public String menuDescription(Actor actor) {
                return actor.hasAbility(Abilities.CREATIVE_MODE)
                        ? "Disable Creative Mode"
                        : "Enable Creative Mode";
            }
        });

        if (__getMorph() == PlayerForm.WOLF) {
            try { actions.add(new SmellTreasureAction(6)); } catch (Throwable __ignore) {}
        }

        if (__getMorph() == PlayerForm.BEAR) {
            try {
                actions.add(new ShakeTreeAction(2, 3));
            } catch (Throwable ignored) {}
        }

        if (__getMorph() == PlayerForm.DRAGON) {
            try { actions.add(new ElementalBreathAction()); } catch (Throwable ignored) {}
            try { actions.add(new FlightMoveAction(2)); } catch (Throwable ignored) {}
        }

        for (Ignite ig : new java.util.ArrayList<>(statusesOf(Ignite.class))) {
            ig.resetTurnFlags();
            ig.onTurn(this, display);
        }


        LunarPhaseManager lunarManager = LunarPhaseManager.getInstance();
        if (lunarManager != null) {
            lunarManager.applyPhaseEffects(map, this);
            lunarManager.advanceTurn();
        }

        if (lastAction == null || lastAction.getNextAction() == null) {
            modifyAttribute(ExplorerAttributes.HYDRATION, ActorAttributeOperation.DECREASE, 1);
            modifyAttribute(ExplorerAttributes.WARMTH, ActorAttributeOperation.DECREASE, 1);
        }

        Location currentLocation = map.locationOf(this);

        if (currentLocation.getGround().getDisplayChar() == '.') {
            for (CoatableWeapon coatable : coatableWeapons) {
                if (!coatable.hasCoating()) {
                    actions.add(new CoatWeaponAction(coatable, new SnowCoatingItem()));
                }
            }
        }

        boolean torchBurn = statusesOf(Ignite.class).stream()
                .anyMatch(i -> i.source() == BurnSource.TORCH);
        if (torchBurn) {
            int turnsLeft = 0;
            for (Ignite b : statusesOf(Ignite.class)) {
                turnsLeft = b.remainingTurns();
            }

            int torchDamage = BurnSource.TORCH.defaultDamagePerTurn();
            while (turnsLeft > 0) {
                this.hurt(torchDamage);
                display.println("Explorer takes " + torchDamage + " fire damage!");
                turnsLeft -= 1;
            }
        }

        display.println(this.toString());

        boolean currentlyAtZero = (getAttribute(ExplorerAttributes.HYDRATION) <= 0 ||
                getAttribute(ExplorerAttributes.WARMTH) <= 0);

        if (wasAtZeroLastTurn && currentlyAtZero) {
            display.println("The Explorer collapses from exhaustion... Game Over!");
            unconscious(map);
            return new DoNothingAction();
        }

        wasAtZeroLastTurn = currentlyAtZero;

        if (this.isConscious()) {
            if (lastAction != null && lastAction.getNextAction() != null) {
                return lastAction.getNextAction();
            }

            for (WeaponWithActions weapon : weapons) {
                actions.add(weapon.allowableActions(this, map));
            }

            for (Item item : map.locationOf(this).getItems()) {
                if (item.getDisplayChar() == '=') {
                    actions.add(new SleepAction(this));
                    break;
                }
            }

            for (WeaponWithActions weapon : weapons) {
                if (!weapon.isRangedWeapon()) {
                    for (Exit exit : map.locationOf(this).getExits()) {
                        Location destination = exit.getDestination();
                        if (destination.containsAnActor()) {
                            Actor target = destination.getActor();
                            if (target.hasAbility(Abilities.CAN_ATTACK)) {
                                actions.add(new AttackAction(target, exit.getName(), weapon));
                            }
                        }
                    }
                }
            }

            addRangedAttackOptions(actions, map);
        } else {
            unconscious(map);
            return new DoNothingAction();
        }

        ActionList menuActions = actions;
        if (!creative) {
            ActionList filtered = new ActionList();
            for (Action a : actions) if (!isCreativeOnly(a, this)) filtered.add(a);
            menuActions = filtered;

        }

        Menu menu = new Menu(menuActions);
        return menu.showMenu(this, display);

    }

    /**
     * Returns a string representation including hydration and warmth stats.
     *
     * @return formatted string with explorer status
     */
    @Override
    public String toString() {
        String form = __getMorph().name();
        return super.toString()
                + " [" + form + "]"
                + " | Hydration: " + getAttribute(ExplorerAttributes.HYDRATION)
                + " | Warmth: " + getAttribute(ExplorerAttributes.WARMTH);

    }

    private static final char EXPLORER_CHAR = 'ඞ';
    private static final char WOLF_CHAR     = 'Ẅ';
    private static final char BEAR_CHAR     = 'Ḃ';
    private static final char DRAGON_CHAR   = 'Ḋ';

    @Override
    public char getDisplayChar() {
        switch (__getMorph()) {
            case WOLF:
                return WOLF_CHAR;
            case BEAR:
                return BEAR_CHAR;
            case DRAGON:
                return DRAGON_CHAR;
            default:
                return EXPLORER_CHAR;
        }
    }




    /**
     * Increases the explorer's hydration by the specified amount.
     *
     * @param amount the amount to increase hydration by
     */
    public void increaseHydration(int amount) {
        modifyAttribute(ExplorerAttributes.HYDRATION, ActorAttributeOperation.INCREASE, amount);
    }

    /**
     * Adds ranged attack options to the action list for all ranged weapons.
     *
     * @param actions the action list to add ranged attacks to
     * @param map     the current game map
     */
    private void addRangedAttackOptions(ActionList actions, GameMap map) {
        for (WeaponWithActions weapon : weapons) {
            ActionList weaponActions = weapon.getRangedAttackActions(this, map);
            for (Action action : weaponActions) {
                actions.add(action);
            }
        }
    }

    /**
     * Returns actions that other actors can perform on this explorer.
     *
     * @param otherActor the actor performing the action
     * @param direction  the direction to the explorer
     * @param map        the current game map
     * @return list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();

        if (otherActor.hasAbility(Abilities.CAN_ATTACK)) {
            actions.add(new AttackAction(this, direction));
        }

        return actions;
    }

    @Override public void hurt(int points) {
        if (hasAbility(Abilities.CREATIVE_IMMUNE)) return; // ignore damage in animal forms
        super.hurt(points);
    }


    // --- Morph state (core) ---
    private PlayerForm __currentForm = PlayerForm.EXPLORER;
    private IntrinsicWeapon __morphedIntrinsic = null;

    // package-private helpers (used by FixedFormState)

    public void __setMorph(PlayerForm form, IntrinsicWeapon intrinsic) {
        this.__currentForm = form;
        this.__morphedIntrinsic = intrinsic; // null => default explorer intrinsic

        // --- Keep morph flags in sync (Fix 2) ---
        // Clear all morph flags first
        disableAbility(game.app.Abilities.FORM_WOLF);
        disableAbility(game.app.Abilities.FORM_BEAR);
        disableAbility(game.app.Abilities.FORM_DRAGON);

        // Creative immunity only while morphed (Explorer = off)
        disableAbility(game.app.Abilities.CREATIVE_IMMUNE);

        switch (form) {
            case WOLF -> {
                enableAbility(game.app.Abilities.FORM_WOLF);
                enableAbility(game.app.Abilities.CREATIVE_IMMUNE);
            }
            case BEAR -> {
                enableAbility(game.app.Abilities.FORM_BEAR);
                enableAbility(game.app.Abilities.CREATIVE_IMMUNE);
            }
            case DRAGON -> {
                enableAbility(game.app.Abilities.FORM_DRAGON);
                enableAbility(game.app.Abilities.CREATIVE_IMMUNE);
            }
            default -> {
                // EXPLORER: no morph flag, no creative immunity
            }
        }
    }
    PlayerForm __getMorph() { return __currentForm; }

    // Classes we want to hide when Creative is OFF (added via reflection so no hard deps)
    private static final java.util.Set<Class<?>> CREATIVE_ONLY_ACTIONS = new java.util.HashSet<>();
    static {
        try { CREATIVE_ONLY_ACTIONS.add(Class.forName("game.actions.potions.BrewPotionAction")); } catch (Throwable ignored) {}
        try { CREATIVE_ONLY_ACTIONS.add(Class.forName("game.actions.potions.ThrowPotionAction")); } catch (Throwable ignored) {}
        try { CREATIVE_ONLY_ACTIONS.add(Class.forName("game.actions.morph.MorphToAnimalAction")); } catch (Throwable ignored) {}
    }
    private static boolean isCreativeOnly(Action a, Actor self) {
        for (Class<?> c : CREATIVE_ONLY_ACTIONS) if (c.isInstance(a)) return true;
        try {
            String m = String.valueOf(a.menuDescription(self)).toLowerCase();
            return m.contains("morph")
                    || m.contains("brew")
                    || (m.contains("potion") && (m.contains("throw") || m.contains("brew") || m.contains("craft")));
        } catch (Throwable ignored) { return false; }
    }


    public game.characters.PlayerForm getCurrentForm() {
        return game.actions.playermorphactions.MorphToAnimalAction.getCurrentForm(this);
    }

}
