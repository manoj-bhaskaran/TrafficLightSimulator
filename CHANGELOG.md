# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

While the project is pre-MVP, releases use the `0.x.y` SemVer range. Minor
version increments may include breaking changes until a stable `1.0.0` release
is declared; patch increments are reserved for backward-compatible fixes within
the current `0.x` baseline.

## [Unreleased]

### Added

- Added JaCoCo coverage instrumentation and XML report generation during
  `mvn verify`, plus SonarQube Cloud coverage report path wiring for CI
  analysis.
- Added validation tests for `Intersection` road-count bounds and add-road
  capacity, plus `Road` angle bounds tests.
- Added traffic-light incompatibility rules to `TrafficLightGroup`, including
  symmetric configuration, removal, lookup, and guarded per-light color/state
  updates that reject incompatible simultaneous `GREEN`/`ON` signals with clear
  feedback.
- Added `Intersection.configureIncompatibleTrafficLights` to register
  incompatibility rules across the road and pedestrian light groups that belong
  to an intersection.
- Added unit tests covering incompatibility configuration, conflict prevention,
  inactive-light allowance, group membership validation, and cross-road
  intersection enforcement.
- Added JUnit Jupiter 5.11 as a test-scoped dependency and configured
  `maven-surefire-plugin` 3.3.1 to run JUnit 5 tests.
- Added `TrafficLightTest` covering `setColor` for all combinations of
  `Type` (TRAFFIC/PEDESTRIAN) and `isMultiColor` (true/false), including
  the null-guard and AMBER rejection for pedestrian lights.

### Fixed

- `TrafficLight.setColor` previously blocked every call on non-multi-colour
  lights regardless of whether the requested colour was valid. The guard has
  been redesigned: PEDESTRIAN lights reject AMBER; all other valid colours are
  accepted for both single- and multi-colour lights; a null argument now throws
  `IllegalArgumentException` with a clear message.

### Changed

- Incremented the pre-MVP development version from `0.3.0-SNAPSHOT` to
  `0.4.0-SNAPSHOT` for the automated testing and coverage framework.
- Updated CI to run SonarQube Cloud analysis with the `sonarcloud` profile so
  scanner execution imports the generated JaCoCo XML coverage report.
- Documented the automated test and coverage workflow in the README.
- Incremented the pre-MVP development version from `0.2.0-SNAPSHOT` to
  `0.3.0-SNAPSHOT` for the traffic-light incompatibility feature.
- Documented traffic-light safety rule configuration and enforcement in the
  README.

- Deleted the top-level `Type.java` enum (`TRAFFIC`, `PEDESTRIAN`), which was
  dead code. The canonical definition is the nested `TrafficLight.Type` enum,
  which is already used throughout the codebase (`TrafficLight`,
  `PedestrianCrossing`).
- Incremented the pre-MVP development version from `0.1.2-SNAPSHOT` to
  `0.2.0-SNAPSHOT`. Deletion of the public top-level `Type` class is a
  breaking API change; per the project versioning policy, breaking changes
  require a minor-version increment in the `0.x` range.

- Added a runnable application entry point that bootstraps and logs a sample
  traffic-light intersection.
- Added executable jar manifest configuration so packaged artifacts can be run
  with `java -jar`.
- Documented Maven and packaged-jar application run commands in the README.
- Established a Keep a Changelog file with an `Unreleased` section.
- Added MIT licensing documentation for the repository.
- Documented the pre-MVP SemVer versioning policy in the README.
- Documented that SonarQube Cloud analysis requires `SONAR_TOKEN` and an explicit
  `sonarcloud` Maven profile opt-in.
- Documented how to troubleshoot Maven Central `403 Forbidden` errors while
  resolving Maven lifecycle plugins.

### Changed

- Incremented the pre-MVP development version from `0.1.1-SNAPSHOT` to
  `0.1.2-SNAPSHOT` for the platform-portable dependency fix.
- Removed unused JavaFX dependencies, including the Windows-only
  `javafx-graphics` classifier, so Maven verification resolves dependencies
  portably on Linux, macOS, and Windows.
- Updated the README to describe the current Java-only runtime dependency
  baseline and the deferred JavaFX GUI dependency policy.
- Incremented the pre-MVP development version from `0.1.0-SNAPSHOT` to
  `0.1.1-SNAPSHOT` for the runnable packaging fix.
- Updated Maven coordinates from the placeholder `com:TrafficLightSimulator` at
  `1.0-SNAPSHOT` to `com.trafficlightsimulator:TrafficLightSimulator` at the
  pre-MVP development baseline `0.1.0-SNAPSHOT`.
- Pinned the SonarQube Cloud Maven scanner plugin version to avoid implicit
  scanner version changes during analysis.
- Configured SonarQube analysis to be skipped by default and enabled only through
  the `sonarcloud` Maven profile, avoiding unauthenticated scanner failures in
  pull-request builds.
- Pinned the Maven resources plugin version used by the verification lifecycle.

## [0.1.0] - 2026-06-04

### Added

- Established the initial pre-MVP SemVer baseline for future project releases.

[Unreleased]: https://github.com/manoj-bhaskaran/TrafficLightSimulator/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/manoj-bhaskaran/TrafficLightSimulator/releases/tag/v0.1.0
