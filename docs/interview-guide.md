# Project interview guide

## 30-second explanation

Winter Survival is a terminal-based Java game built around an object-oriented turn engine. I implemented survival attributes, consumable resources, timed food-producing trees, combat, and a taming system for deer, wolves, and bears. The main engineering focus is modularity: actions depend on small capability interfaces, while NPC decisions are composed from priority-ordered behaviour strategies. I also modelled the system with focused UML class diagrams and added a Maven build with JUnit coverage for core rules.

## One-minute technical explanation

The entry point creates an `Earth` world containing a 40-by-10 snow map, a player, three animal types, and three resource-producing trees. The supplied `World` loop ticks the map, creates the actions available to each actor from its inventory and surroundings, then asks the actor to choose an action. Player actions go through a menu; animals select the first applicable strategy from a priority-ordered behaviour map. I extended the engine with capability interfaces such as `Eatable`, `Sleepable`, and `Tamable`, which keep actions independent of concrete classes. Taming changes ownership state and changes effective AI: predators stop attacking indiscriminately, follow the owner, and protect them, while deer gather food. The strongest design choices are command-style actions, strategy-based AI, clear game-versus-engine package boundaries, and UML documentation that makes the relationships explicit.

## Architecture walkthrough

1. `game.Application.main` creates the display and `Earth`.
2. `Earth.constructWorld` registers snow terrain, constructs the map, places the player and animals, and replaces three snow tiles with resource trees.
3. `World.run` initialises each actor's last action and repeats while the player remains in the actor/location registry.
4. Each iteration ticks every map, draws the player's map, and processes actors with the player first.
5. `World.prepareAllowableActions` collects inventory, terrain, movement, adjacent-actor, ground-item, pickup/drop, and wait actions.
6. `Player.playTurn` displays stats and delegates selection to `Menu`. Animal `playTurn` methods ask behaviours in priority order.
7. The selected `Action.execute` mutates the relevant actor, item, or map state and returns a message.
8. `Action.getNextAction` supports continuation; sleeping uses it to span multiple turns.

## Important classes

### `Application`

**PURPOSE:** Program entry point.  
**RESPONSIBILITY:** Create the display, show the title, construct the world, and start the loop.  
**INTERACTS WITH:** `Display`, `Earth`, `FancyMessage`.  
**WHY IT EXISTS:** Keeps startup concerns separate from world configuration and gameplay.  
**IMPORTANT METHODS:** `main`.  
**SOFTWARE ENGINEERING CONCEPT:** Composition root / dependency construction.

### `Earth`

**PURPOSE:** Concrete game-world configuration.  
**RESPONSIBILITY:** Create the map and place actors and trees.  
**INTERACTS WITH:** `World`, `GameMap`, `DefaultGroundCreator`, all initial actors and trees.  
**WHY IT EXISTS:** Separates reusable engine flow from this game's concrete content.  
**IMPORTANT METHODS:** `constructWorld`.  
**SOFTWARE ENGINEERING CONCEPT:** Inheritance used as a framework extension point.

### `Player`

**PURPOSE:** Human-controlled survival actor.  
**RESPONSIBILITY:** Initialise player abilities, attributes, equipment, display statistics, and select menu actions.  
**INTERACTS WITH:** `Actor`, `Menu`, `GameAttributes`, `Bedroll`, `Bottle`.  
**WHY IT EXISTS:** Adds game-specific survival rules to the generic actor abstraction.  
**IMPORTANT METHODS:** `playTurn`, `isConscious`, `displayStats`, `updateTurn`.  
**SOFTWARE ENGINEERING CONCEPT:** Polymorphic actor lifecycle and encapsulated state.

### `Bear`, `Wolf`, and `Deer`

**PURPOSE:** Wildlife and potential companions.  
**RESPONSIBILITY:** Hold taming state, choose an action from behaviours, and expose contextual interactions to other actors.  
**INTERACTS WITH:** `Tamable`, `Behaviour`, `AttackAction`, `TameAction`, intrinsic weapons.  
**WHY THEY EXIST:** Give each animal a distinct configuration while sharing engine and capability contracts.  
**IMPORTANT METHODS:** `playTurn`, `allowableActions`, `tame`, `isTamed`, `getOwner`.  
**SOFTWARE ENGINEERING CONCEPT:** Inheritance from `Actor`, interface realisation, and behaviour composition.

### `Tamable`

**PURPOSE:** Contract for any animal that can acquire an owner.  
**RESPONSIBILITY:** Expose the taming transition and ownership query.  
**INTERACTS WITH:** `TameAction`, `FollowBehaviour`, `ProtectOwnerBehaviour`, `CollectFoodBehaviour`.  
**WHY IT EXISTS:** Prevents actions and behaviours from depending on `Bear`, `Wolf`, or `Deer` directly.  
**IMPORTANT METHODS:** `tame`, `isTamed`, `getOwner`.  
**SOFTWARE ENGINEERING CONCEPT:** Abstraction, polymorphism, and dependency inversion.

### `Action` and game actions

**PURPOSE:** Represent a possible decision as an object.  
**RESPONSIBILITY:** Execute one domain operation and describe it to the menu.  
**INTERACTS WITH:** `Actor`, `GameMap`, and a narrow capability such as `Eatable` or `Tamable`.  
**WHY IT EXISTS:** Lets the world assemble context-specific commands without hard-coding every operation into the loop.  
**IMPORTANT METHODS:** `execute`, `menuDescription`, `getNextAction`.  
**SOFTWARE ENGINEERING CONCEPT:** Command pattern and runtime polymorphism.

### `Behaviour` and game behaviours

**PURPOSE:** Reusable NPC decision policies.  
**RESPONSIBILITY:** Inspect actor/world state and return one useful action, or `null` when inapplicable.  
**INTERACTS WITH:** `Actor`, `GameMap`, `Tamable`, and action classes.  
**WHY IT EXISTS:** Avoids monolithic AI methods and permits different animals to reuse the same decision logic.  
**IMPORTANT METHODS:** `generateAction`.  
**SOFTWARE ENGINEERING CONCEPT:** Strategy pattern and composition over inheritance.

### `Apple`, `Hazelnut`, and `YewBerry`

**PURPOSE:** Concrete food items with distinct outcomes.  
**RESPONSIBILITY:** Advertise the food ability, expose `EatAction`, and apply their effect when eaten.  
**INTERACTS WITH:** `Item`, `Eatable`, `EatAction`, actor attributes.  
**WHY THEY EXIST:** Keep each consumable's effect close to the item instead of building a type switch in `EatAction`.  
**IMPORTANT METHODS:** `allowableActions`, `eatenBy`.  
**SOFTWARE ENGINEERING CONCEPT:** Interface-based polymorphism and Open/Closed Principle.

### `AppleTree`, `HazelnutTree`, and `YewBerryTree`

**PURPOSE:** Renewable world-resource producers.  
**RESPONSIBILITY:** Count turns and place one resource on a random passable adjacent tile at the configured interval.  
**INTERACTS WITH:** `Ground`, `Location`, `Exit`, and the corresponding food item.  
**WHY THEY EXIST:** Model resource generation as terrain lifecycle behaviour.  
**IMPORTANT METHODS:** `tick` and the private drop method.  
**SOFTWARE ENGINEERING CONCEPT:** Framework callback override and domain modelling.

### `World` (supplied engine)

**PURPOSE:** Coordinate the whole turn-based simulation.  
**RESPONSIBILITY:** Tick maps, draw the active map, build action lists, process actors, and decide when the game ends.  
**INTERACTS WITH:** `GameMap`, `Actor`, `Action`, `Location`, `Item`, `Display`.  
**WHY IT EXISTS:** Centralises orchestration while delegating domain behaviour to polymorphic objects.  
**IMPORTANT METHODS:** `run`, `gameLoop`, `processActorTurn`, `prepareAllowableActions`.  
**SOFTWARE ENGINEERING CONCEPT:** Framework orchestration; supplied code, not claimed as original implementation.

## Where core OOP concepts appear

- **Inheritance:** `Player`, `Bear`, `Wolf`, and `Deer` extend `Actor`; foods extend `Item`; trees and `Snow` extend `Ground`; custom actions extend `Action`.
- **Composition:** animals contain maps of `Behaviour` objects; actors contain inventory items and attribute objects; the world contains maps.
- **Polymorphism:** the world treats every decision as `Action`; behaviours return any action implementation; `EatAction` works with any `Eatable`.
- **Encapsulation:** inventory and ability collections are controlled by engine methods and exposed with unmodifiable views.
- **Abstraction:** capability interfaces express what an object can do without revealing its concrete type.

## Design decisions to discuss

1. **Why an interface for taming?** It lets otherwise different actor subclasses opt into ownership without consuming Java's single inheritance slot. The cost is repeated `tamed` and `owner` fields.
2. **Why behaviours return actions?** Decision logic stays separate from mutation. The same action execution path works for player and NPC choices.
3. **Why a `TreeMap` for behaviours?** Sorted integer keys make priority deterministic and easy to reconfigure. An enum or named priority constants would be safer than magic integers in a larger system.
4. **Why capability interfaces for items?** `EatAction` and `DrinkAction` remain closed to modification when new foods or drinks are added.
5. **Why trees extend `Ground`?** The engine already ticks ground each turn, providing a natural lifecycle hook. The trade-off is that a tree replaces the snow terrain for that tile.
6. **Why retain the supplied engine unchanged?** It preserves the ownership boundary and reduces risk. Improvements are focused in the game layer and tests.

## Likely Java/OOP questions

- What is the difference between `extends` and `implements` in this codebase?
- Why is `Tamable` an interface instead of an abstract animal base class?
- How does dynamic dispatch choose the correct `execute`, `eatenBy`, or `generateAction` implementation?
- Why return unmodifiable inventory and exit lists?
- What benefit does `Map<Enum<?>, ActorAttribute<Integer>>` provide, and what type-safety trade-offs does it make?
- Where is composition preferable to inheritance in the animal AI?
- Is `Action` a Command pattern implementation? What are the command receiver and invoker?
- How would you unit-test random combat, random resource placement, or sleep duration?
- What happens when two behaviours are applicable on the same turn?
- How would you add a new animal, consumable, or survival attribute without modifying existing classes?

## Weaknesses and honest responses

- **No victory state:** “The current game is an open-ended survival sandbox. I would add a milestone or score in a dedicated objective component instead of embedding it in the player.”
- **Warmth lacks a direct restoration action:** “Warmth is a loss condition, while chained sleep pauses the player's resource decay. A better next iteration would make camp or shelter mechanics restore warmth explicitly.”
- **Repeated animal code:** “I chose the flexible `Tamable` interface, but the three actors duplicate ownership and behaviour-loop code. I would extract a small reusable component or carefully scoped base class after adding regression tests.”
- **Greedy pathfinding:** “Following uses Manhattan distance and can stall around obstacles. A breadth-first or A* pathfinder behind a navigation interface would scale better.”
- **Randomness and delays are constructed internally:** “That keeps the original implementation simple but makes deterministic testing harder. I would inject `RandomGenerator` and a clock/sleeper abstraction.”
- **Supplied engine has no included licence grant:** “I preserved all author notices and documented the boundary. I would confirm redistribution rights before making the repository public.”
- **Limited automated coverage:** “The added tests cover core domain rules, but menu input and complete game-loop scenarios still need integration tests.”

## What I would improve if rebuilding it

I would keep the action/capability/behaviour separation, then introduce immutable configuration objects for actors and items, a reusable companion-state component, injected random and timing services, obstacle-aware navigation, and an explicit objective system. I would also place the supplied engine behind a declared dependency or replace it with an independently licensed engine so the repository has a clean distribution story.
