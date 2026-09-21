# Attribution and ownership notes

This repository separates the game-specific implementation from a supplied Java game engine.

## Game implementation

The survival systems under `src/main/java/game` are primarily attributed in source to **Esaam Matudin**. They include survival attributes, consumable items, timed resource-producing trees, animal types, taming, and companion behaviours.

Several game-package files were built from supplied examples and retain or restore that provenance:

- `game.FancyMessage`, `game.Snow`, and `game.weapons.BareFist` retain attribution to Adrian Kristanto.
- `game.actors.Player` is based on a supplied player scaffold by Adrian Kristanto and was extended by Esaam Matudin with survival attributes, starting equipment, status display, and survival turn handling.
- `game.actions.AttackAction` was adapted from a supplied forest demonstration by Adrian Kristanto.
- `game.behaviours.WanderBehaviour` was adapted from a supplied implementation by Riordan D. Alfredo.
- `game.behaviours.FollowBehaviour` was adapted from a supplied implementation by Riordan D. Alfredo to target a `Tamable` animal's owner.

## Supplied engine

The code under `src/main/java/edu/monash/fit2099/engine` is a supplied engine and is not presented as Esaam Matudin's original work. Its source-level author notices identify Riordan Alfredo and Adrian Kristanto. Those notices have been preserved.

The engine provides the world loop, map and location model, actor and item base classes, action menu, capability/status support, attributes, inventory plumbing, movement, and intrinsic weapon abstraction. The game-specific package extends those APIs.

## Distribution status

The source material did not include a licence file or an explicit redistribution grant for the supplied engine. No licence has therefore been added to this repository. Before making the repository public, the owner should confirm that the engine may be redistributed publicly or replace it with code they have the right to publish.

The bundled demonstration applications and source-archive planning files are intentionally excluded. They are not required to build or run the game and included private metadata.
