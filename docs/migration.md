# Portfolio migration

Earlier portfolio: `6f6ebc552accc5b80c24d09561eab80bcb954cab`. Team snapshot: `2df50b653befaf4e36ddb63e0f946b116a425dd2`, 3 November 2025.

| Earlier version | Team-based portfolio |
| --- | --- |
| One snow environment | Forest/Plains and teleportation |
| Fixed tree production | Stages, map-specific rules, ordered tree behaviours |
| Basic wildlife/taming | Spawners, crocodiles/poison, post-spawn effects, elemental monsters |
| Survival equipment | Additional weapons/statuses, crafting, player forms, lunar effects |
| Eight regressions | 109 imported team tests plus eight adapted regressions |
| game.Application | game.app.Application |
| No runtime libraries | OkHttp/JSON and runtime libraries beside the JAR |

Gameplay was replaced with the team's later implementation to avoid duplicate conflicting versions. The engine and unrelated metadata are preserved. Earlier tests were adapted for packages, constructors, consumption actions, increased initial attributes, and mature stages. Old docs and the gameplay image are explicitly archived; the original code remains in Git history.

Generated classes, target output, IDE metadata, unrelated demo apps, student identifiers, private logs, and video-submission links are excluded. Source authors, teammates, engine credits, and original UML are retained. Reviewed summaries preserve design rationale without student metadata. No broad architectural rewrite was undertaken.
