# Team systems and original diagrams

These team features are not claimed as Esaam's Assignment 3 contribution.

| Area | Main credited contributor | Concrete design |
| --- | --- | --- |
| Spawning/crocodiles | Sai Sann Thet Lone | AnimalSpawner/configuration select animals; PostSpawnEffect implementations handle poison, drops, and flora effects. |
| Crafting/elemental effects | Taiyo Arima | Potion extends Item; BrewAction consumes ingredients; AreaEffect strategies and AoE handle bounded areas. |
| Player morphing | Taiyo Arima | Action classes offer forms; flags gate abilities; movement wrappers provide speed/flight. |
| Lunar/survival | Taabish Farooq Bhat | MoonPhaseService separates HTTP from coordination; enum stores phase data; manager handles effects/fallback. |

Original team UML: [spawning](spawning-uml.png), [crafting](crafting-uml.png), [player morphing](player-morphing-uml.png), [lunar systems](lunar-uml.png).

Historical diagrams contain simplifications/inconsistencies; source is authoritative. Current player morph actions are under `game.actions.playermorphactions`, distinct from monster states. The HTTP adapter is an attempted integration, not proof of a currently working astronomy endpoint. The large lunar effect method and coordinated player flags remain maintenance limitations.

Original rationale PDFs remain in the [private upstream](https://git.infotech.monash.edu/fit2099/fit2099-s2-2025/assignment-groups/MA_Applied02_GroupG/project/-/tree/main/docs/design/assignment3). Reviewed summaries preserve technical decisions and credit without publishing student identifiers.
