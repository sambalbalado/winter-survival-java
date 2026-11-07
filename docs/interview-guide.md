# Interview guide

**Scope:** Winter Survival is a four-person Java game on a supplied engine. My Assignment 3 work focused on flora growth/production, the tree interface, tests, UML, and merging changes into the shared codebase.

**Decision:** Tree.tick increments its lifecycle counter and invokes TreeBehaviour operations in sorted priority order. Growth runs before production. The interface separates scheduling from operations; Stage explicitly names lifecycle states.

**Tradeoff:** Concrete behaviours retain map/stage conditionals. Growth replaces ground instances and transfers the fruit counter. Configuration/policy objects are possible improvements, not completed features.

**Testing:** The two original flora suites have 35 JUnit/Mockito methods. Mocks isolate maps/locations and fruit placement. Real-map portfolio regressions add another boundary. Some original assertions are permissive and random growth is not injected.

**Collaboration:** Use the authored interface, implementation, test, rationale, and merge evidence in [ATTRIBUTION.md](../ATTRIBUTION.md). Attribute player morphing/crafting, spawning, and lunar systems to teammates.

**Portfolio integration:** Imports the team game/tests, keeps the engine unchanged, preserves notices/history, adapts eight regressions, and packages an executable JAR with separate dependencies. It does not rewrite the architecture.
