package game.morph;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.playermorphactions.MorphToAnimalAction;
import game.app.Abilities;
import game.characters.Player;
import game.characters.PlayerForm;
import game.environment.Dirt;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;


@DisplayName("REQ4: MorphToAnimalAction")
class MorphToAnimalActionTest {

    private GameMap map3x3;
    private Location start;
    private Player player;

    @BeforeEach
    void setUp() throws GameEngineException {

        player = new Player();

        // a tiny 3x3 real map only for creating a Location object
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);
        GameMap real = new GameMap("Req4Map", creator, java.util.Arrays.asList("...", "...", "..."));
        Location start = real.at(1, 1);

        // mock the map used by execute()
        map3x3 = mock(GameMap.class);
        when(map3x3.locationOf(any(Actor.class))).thenReturn(start);
        // if execute() calls other GameMap methods, stub them similarly
    }
    // ---------------------- menuDescription ----------------------

    @Test
    @DisplayName("menuDescription shows 'actor → label'")
    void should_showActorArrowLabel_in_menuDescription() {
        var action = new MorphToAnimalAction("Morph: Wolf", Abilities.FORM_WOLF, null);

        String text = action.menuDescription(player);

        assertTrue(text.contains(player.toString()));
        assertTrue(text.contains("→"));
        assertTrue(text.endsWith("Morph: Wolf"));
    }

    // ---------------------- execute: success path ----------------------

    @Test
    @DisplayName("execute morphs Player to WOLF when privileged")
    void should_morphToWolf_when_playerPrivileged() {
        // Arrange: allow morphing
        player.enableAbility(Abilities.MORPH_PRIVILEGED);
        var action = new MorphToAnimalAction("Morph: Wolf", Abilities.FORM_WOLF, null);

        // Act
        String result = action.execute(player, map3x3);

        // Assert (message + state)
        assertTrue(result.toLowerCase().contains("morphs into wolf"));
        assertTrue(player.hasAbility(Abilities.FORM_WOLF));
        // If you didn’t add Player#getCurrentForm(), use the static helper:
        assertEquals(PlayerForm.WOLF, MorphToAnimalAction.getCurrentForm(player));
    }

    @Test
    @DisplayName("execute reverts Player back to EXPLORER")
    void should_revertToExplorer_when_revertAction() {
        // morph first
        player.enableAbility(Abilities.MORPH_PRIVILEGED);
        new MorphToAnimalAction("Morph: Wolf", Abilities.FORM_WOLF, null).execute(player, map3x3);
        assertTrue(player.hasAbility(Abilities.FORM_WOLF));

        // revert
        String msg = new MorphToAnimalAction("Revert to Explorer", null, null).execute(player, map3x3);

        assertTrue(msg.toLowerCase().contains("reverts to explorer"));
        assertFalse(player.hasAbility(Abilities.FORM_WOLF));
        assertEquals(PlayerForm.EXPLORER, MorphToAnimalAction.getCurrentForm(player));
    }

    // ---------------------- execute: blocked paths ----------------------

    @Test
    @DisplayName("execute is blocked if actor is NOT Player")
    void should_block_when_actorIsNotPlayer() {
        var npc = new DummyActor();
        var action = new MorphToAnimalAction("Morph: Wolf", Abilities.FORM_WOLF, null);

        String msg = action.execute(npc, map3x3);

        // message & state checks
        assertNotNull(msg);
        assertTrue(msg.toLowerCase().contains("cannot morph")); // matches "cannot morph in this mode"
        assertFalse(npc.hasAbility(Abilities.FORM_WOLF));
        assertEquals(PlayerForm.EXPLORER, MorphToAnimalAction.getCurrentForm(npc));
    }

    @Test
    @DisplayName("execute is blocked if Player lacks MORPH_PRIVILEGED")
    void should_block_when_playerNotPrivileged() {
        var action = new MorphToAnimalAction("Morph: Bear", Abilities.FORM_BEAR, null);

        String msg = action.execute(player, map3x3);

        assertTrue(msg.toLowerCase().contains("cannot morph"));
        assertFalse(player.hasAbility(Abilities.FORM_BEAR));
        assertEquals(PlayerForm.EXPLORER, MorphToAnimalAction.getCurrentForm(player));
    }

    // ---------------------- minimal doubles ----------------------

    /** Non-player actor used to assert blocking behavior. */
    static final class DummyActor extends Actor {
        DummyActor() { super("NPC", 'n', 10); }
        @Override public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }
}
