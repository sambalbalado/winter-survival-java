# Attribution and provenance

## Four-person team

This game incorporates the FIT2099 Assignment 3 version developed by **Esaam Matudin, Sai Sann Thet Lone, Taiyo Arima, and Taabish Farooq Bhat**. It is not Esaam's solo implementation. Student identifiers, submission boilerplate, private contribution spreadsheets, grades, and handover-video links are excluded.

[Original repository](https://git.infotech.monash.edu/fit2099/fit2099-s2-2025/assignment-groups/MA_Applied02_GroupG/project) (Monash access required). Imported gameplay/test snapshot: `2df50b653befaf4e36ddb63e0f946b116a425dd2`, 3 November 2025. Java author notices are preserved.

| Contributor | Verified areas; not an exhaustive ownership allocation |
| --- | --- |
| Esaam Matudin | Assignment 3 flora growth/production, tree-behaviour interface/ordering, tree tests, flora UML/rationale, feature-branch integration. Earlier source also credits taming/animal behaviours and monster states to Esaam. |
| Sai Sann Thet Lone (also credited as Sai Sann Lone) | World/map setup, animals/spawning, crocodile/poison/post-spawn effects, tests and design documents. |
| Taiyo Arima | Teleportation, crafting/area effects, player morphing/abilities, associated tests/designs, final integration/documentation. |
| Taabish Farooq Bhat | Player survival/equipment, consumables, original flora base, weapons, lunar service/effects/berries, associated tests/designs. |

Shared-file headers do not establish exclusive ownership. Esaam's earlier monster states differ from Taiyo's later **player** morphing system. The resume focuses on the more narrowly verified flora work.

## Esaam's Assignment 3 evidence

Authored commits were checked against current source and documentation:

- [`36330019`](https://git.infotech.monash.edu/fit2099/fit2099-s2-2025/assignment-groups/MA_Applied02_GroupG/project/-/commit/363300190706f7fe8c85255351d9a86d46f3ea3f): introduces `TreeBehaviour`.
- [`c0ed3c45`](https://git.infotech.monash.edu/fit2099/fit2099-s2-2025/assignment-groups/MA_Applied02_GroupG/project/-/commit/c0ed3c45b87fdaa0ff11d960299f6656b1d31daf): adds ordered behaviours and `addBehaviour` to `Tree`.
- [`e42a5861`](https://git.infotech.monash.edu/fit2099/fit2099-s2-2025/assignment-groups/MA_Applied02_GroupG/project/-/commit/e42a58614a79168d6c11e0699f8e796d7498be48): adds tree unit tests/Maven test support and reorganises source/test directories.
- `c69ae624`, `4a9f0de2`, `6d7ff0b2`, `9f0913ae`: tree implementation changes in Esaam's author-filtered history.
- `5bf85d1e`, `1337c509`, `a8f90b67`: flora UML/rationale; `b0d43eca`: merges the flora branch into main.

The 35-method count comes from the two imported flora test files. Adapted portfolio regression tests are not claimed as original Assignment 3 work.

## Supplied engine and examples

`src/main/java/edu/monash/fit2099/engine` is supplied code by **Riordan Alfredo and Adrian Kristanto**, not original team work. It provides the world loop, maps/locations, actors/items, actions/menu, capabilities, attributes, inventory, and movement.

The existing portfolio engine is unchanged in this update. Five files differ from the team snapshot only in comments or unused imports. Source notices remain intact. Game classes adapted from supplied examples include `FancyMessage`, `BareFist`, player/action scaffolds, and wandering/following behaviours; existing notices and the earlier portfolio provenance still apply.

## Design materials

Original UML diagrams in `docs/design/`: flora by Esaam, spawning by Sai Sann, crafting/player morphing by Taiyo, lunar systems by Taabish. These historical diagrams are not exact, exhaustive models of every current class. Reviewed Markdown summaries replace student/submission metadata and explain discrepancies between rationale and actual code.

## AI assistance

The team acknowledgement records Claude assistance with implementation, debugging, test ideas, Javadocs, and design documentation. The team retained responsibility for decisions, integration, and explaining the work. This portfolio integration and resume update were prepared with ChatGPT assistance. AI assistance does not change teammate or engine attribution.

## Distribution status

The supplied material contained no licence file or explicit redistribution grant for the engine or team code. This update does not invent an open-source licence or claim ownership of those components. Dependencies retain their licences/notices in their JARs. No repository licence has been added.
