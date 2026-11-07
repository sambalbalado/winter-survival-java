# Validation and known limitations

Validated on 7 November 2026. Maven 3.9.11 used Homebrew OpenJDK 25.0.1 to compile with `--release 17`; the interactive packaged game was launched with OpenJDK 23.0.2 on macOS arm64.

## Completed checks

| Check | Result |
| --- | --- |
| Earlier portfolio baseline: `mvn -o clean test` | Eight tests passed before import. |
| Integrated game: `mvn -o clean verify` | Build, packaging, and 117 tests passed; zero failures/errors/skips. |
| Test breakdown | 109 imported team tests plus eight adapted portfolio regressions; 35 methods in the two original flora suites. |
| Executable JAR | Manifest points to `game.app.Application` and lists nine runtime libraries under `dependency/`. |
| Interactive smoke check | World/menu rendered; creative mode enabled; player transformed into Wolf; teleport cube exercised its malfunction path and then successfully moved the player to Plains (10,2). Process stopped with Ctrl+C. |
| Service failure path | Startup continued with NORMAL phase when the HTTP service could not be reached. Unit tests also exercise unavailable/throwing service doubles. |
| Runtime setup | Documented dependency-copy command passed and prepared all nine runtime libraries. |
| Engine boundary | Existing engine files are unchanged. Team engine differences were comments/unused imports only. |

Maven/Surefire emits a JVM deprecation warning in this environment. Test output also includes intentional error messages from service-failure tests; the XML reports have zero test errors.

## Runtime dependencies

The documented `maven-dependency-plugin:3.7.1:copy-dependencies -DincludeScope=runtime` command passed and prepared all nine runtime libraries listed in the executable JAR manifest. Keep `target/dependency/` beside the JAR. No libraries or build output are committed.

The build/test/package check used cached dependencies/plugins; the dependency-copy step was also verified through the normal local Maven environment. A completely empty-cache machine was not tested. Successful live astronomy data was not verified and is not required for the fallback game.

## Known limitations

- Tree rules still branch on stage and literal map names; adding biomes requires edits. Growth replaces ground instances and transfers counters.
- Several original tests have permissive assertions; probabilistic growth is not fully deterministic under test. Passing 117 tests does not imply exhaustive gameplay or coverage.
- The lunar manager handles several effect responsibilities. The imported HTTP endpoint/response contract remains unverified for current service availability.
- Player creative/form flags and related menu logic are tightly coordinated; survival stats may still decay while creative mode is enabled. This version is a sandbox rather than a balanced survival release.
- Some output bypasses `Display`; constants/Javadocs/UML completeness remain inconsistent. Original diagrams are historical artifacts.
- No broad refactor, performance claim, test-coverage percentage, or claim of production readiness is made.
