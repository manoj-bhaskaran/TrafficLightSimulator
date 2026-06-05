# Traffic Light Simulator

Traffic Light Simulator is a Java 17 library for modelling traffic-light
intersection domain objects. It provides a typed, validated model of roads,
lanes, traffic lights, pedestrian crossings, and safety incompatibility rules,
with a thin engine layer for simulation lifecycle coordination.

## Current status

The project is **pre-MVP** (versions `0.x.y`). The domain model is under
active development; minor-version increments may introduce breaking changes
until a stable `1.0.0` release is declared.

## Project metadata

- **Maven group ID:** `com.trafficlightsimulator`
- **Maven artifact ID:** `TrafficLightSimulator`
- **Current development version:** `0.17.0-SNAPSHOT`
- **Java baseline:** Java 17

## Functional overview

For a plain-language explanation of what the application does and a catalogue of
the business rules it enforces (intersection layout, road/lane limits, turn
restrictions, traffic-light safety, and pedestrian crossings), see
[docs/FUNCTIONAL_OVERVIEW.md](docs/FUNCTIONAL_OVERVIEW.md). It is written for a
lay reader and does not assume any Java knowledge.

## Architecture overview

The source is organized into four layered packages under
`com.trafficlightsimulator`:

```
com.trafficlightsimulator
├── app      – Runnable entry point (TrafficLightSimulator.main)
├── model    – Domain objects: Intersection, Road, Lane, TrafficLight,
│              TrafficLightGroup, PedestrianCrossing, PedestrianButton,
│              Color, Direction, State
├── engine   – Simulation lifecycle coordination (TrafficLightSimulationEngine)
└── config   – Validation constants, compatibility limit facades,
               and fluent Road/Intersection builders
```

**Key design decisions:**

- Collection getters return read-only views. Structural changes must go
  through typed mutator methods so validation and safety rules stay enforced.
- Core model entities (`Intersection`, `Road`, `Lane`, `TrafficLight`,
  `TrafficLightGroup`, `PedestrianCrossing`, and `PedestrianButton`) use
  Java object identity for equality. This lets simulator relationships target
  specific physical objects rather than value-equal copies.
- A light is considered *active* when its colour is `GREEN` and its state
  is `ON`. Incompatibility checks fire only on activation, keeping the model
  simple for non-conflicting state transitions.

## Versioning policy

This repository follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Because the project is still pre-MVP, versions remain in the `0.x.y` range until
a stable `1.0.0` release is ready.

During the pre-MVP phase:

- `0.MINOR.0` releases may introduce breaking changes as the design evolves.
- `0.x.PATCH` releases are reserved for backward-compatible fixes within the
  current pre-MVP baseline.
- Development builds keep the `-SNAPSHOT` qualifier in `pom.xml`.

Notable changes are tracked in [CHANGELOG.md](CHANGELOG.md), using the Keep a
Changelog format.

## Building and verifying

Run the Maven verification lifecycle from the repository root:

```sh
mvn -B verify
```

## Automated tests and coverage

The repository uses JUnit Jupiter for unit tests, Maven Surefire to run tests in
the standard Maven lifecycle, and JaCoCo to generate coverage. Run the full
verification lifecycle to compile the project, execute tests, and write the XML
coverage report consumed by SonarQube Cloud:

```sh
mvn -B verify
```

The JaCoCo XML report is generated at
`target/site/jacoco/jacoco.xml`. The model package unit-test suite covers
traffic-light validation and group safety rules, lane routing constraints, and
pedestrian button/crossing request lifecycles so coverage gates exercise the
core domain behavior.

## Running the application

Run the simulator entry point directly with Maven:

```sh
mvn exec:java
```

Build the executable jar package, then launch it with `java -jar`:

```sh
mvn -B package
java -jar target/TrafficLightSimulator-0.17.0-SNAPSHOT.jar
```

## Generating API documentation

Generate the Javadoc HTML site with:

```sh
mvn javadoc:javadoc
```

The output is written to `target/site/apidocs/index.html`.

### Resolving Maven Central 403 errors

If `mvn -B verify` fails while downloading `maven-resources-plugin:3.3.1` with
`403 Forbidden`, Maven reached the repository but the network, proxy, or mirror
rejected the request. This repository pins `maven-resources-plugin` to `3.3.1`
through `maven.resources.plugin.version`, so the failure is environmental rather
than a missing project version declaration.

To resolve it:

1. Confirm the network can reach Maven Central:

   ```sh
   curl -I https://repo.maven.apache.org/maven2/
   ```

2. If you are behind a corporate proxy, configure Maven proxy settings in
   `~/.m2/settings.xml` or use the approved internal Maven mirror/artifact
   repository.
3. If a proxy or mirror outage produced a cached failed download marker, clear the
   relevant `*.lastUpdated` files from `~/.m2/repository` or rerun Maven with
   forced updates:

   ```sh
   mvn -U -B verify
   ```

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.
It documents the expected branch names, Commitizen-style commit messages,
CHANGELOG and pre-MVP SemVer updates, CI/test requirements, and the recommended
`main` branch-protection policy.

## Fluent builders and validation constants

Use `RoadBuilder` and `IntersectionBuilder` from the `config` package when you
want construction-time validation with a fluent API. Builders use the shared
`ValidationConstants` values for road angles, lane counts, road-count bounds, and
minimum road-angle spacing, so new construction paths and model mutators enforce
the same limits. `IntersectionBuilder.build()` validates configured roads before
attaching any of them, which keeps failed builds from partially owning roads. The
older `RoadLimits` and `IntersectionLimits` classes remain as compatibility
facades that delegate to `ValidationConstants`.

Example:

```java
Road north = RoadBuilder.atAngle(0.0)
        .lanes(2, 2)
        .build();
Road east = RoadBuilder.atAngle(90.0).build();

Intersection intersection = IntersectionBuilder.withRoadCapacity(2)
        .addRoad(north)
        .addRoad(east)
        .build();
```

## Model collection encapsulation

Collection getters on core model objects expose read-only views of their backing
collections. Callers can inspect roads, lanes, traffic lights, and allowed lane
routes through getters, but structural changes must go through the model methods
such as `Intersection.addRoad`, `Road.setNumIncomingLanes`,
`Road.setNumOutgoingLanes`, `TrafficLightGroup.addTrafficLight`, and
`Lane.addAllowedOutgoingLane` so validation and safety rules remain enforced.

## Intersection road-angle validation

`Intersection.addRoad` enforces a minimum angular separation between every pair
of roads connected to the same intersection. A candidate road must be at least
`Intersection.MIN_ANGLE_BETWEEN_ROADS` degrees from each existing road, using the
shortest circular difference so roads near the `0`/`360` degree boundary are
validated correctly. Roads that violate the spacing rule are rejected with an
`IllegalArgumentException` and are not added to the intersection. After a road
is connected to an intersection, `Road.setAngle` also checks the same spacing
rule before changing the angle, so retained `Road` references cannot move
connected roads into an invalid intersection layout.

## Lane turn restrictions

Inbound `Lane` instances own the set of outbound lanes they may turn into. Use
`Lane.addAllowedOutgoingLane` for incremental configuration or
`Lane.setAllowedOutgoingLanes` to replace an inbound lane's complete restriction
set. The getter returns a read-only view, and duplicate outbound lanes are ignored
so each permitted turn appears only once. Lane membership is identity-based: two
lanes with the same direction are still different physical lanes unless they are
the same object. Before routing traffic, call `Lane.validateOutgoingLaneAllowed`
to reject illegal turns with a clear `IllegalStateException`.

`Road` provides road-level helpers for configuring and enforcing the same
restrictions while ensuring the inbound lane and outbound lane both belong to
that road. Use `Road.addAllowedTurn`, `Road.setAllowedTurns`,
`Road.getAllowedTurns`, `Road.isTurnAllowed`, and `Road.validateTurnAllowed` to
manage inbound-to-outbound movement rules across road lanes.

## Model logging and diagnostics

Routine model mutations such as setter calls, adding lanes/lights, and configuring
turn or incompatibility relationships log at `FINE` rather than `INFO` so normal
simulation runs are not noisy by default. Warnings remain reserved for skipped or
invalid state changes. Each core model entity also provides a concise `toString()`
summary so diagnostic logs show useful object details instead of JVM identity
strings.

## Traffic-light safety rules

`TrafficLightGroup` can define incompatible traffic-light pairs that must never
show active, conflicting signals at the same time. In this model, a light is
considered active when it is `GREEN` and `ON`. Use `addIncompatibleLights` to
configure a pair and update managed lights through `setLightColor` or
`setLightState` so the group can reject unsafe activation attempts with a clear
`IllegalStateException`.

For lights controlled by different road or pedestrian groups in an
`Intersection`, call `configureIncompatibleTrafficLights`. The intersection
locates the owning groups and registers the incompatibility in each affected
group, so either group prevents simultaneous conflicting green signals.

## Pedestrian crossing model

`PedestrianCrossing` owns the pedestrian `TrafficLightGroup` for that crossing.
When a crossing is constructed with start and/or end `PedestrianButton` instances,
the crossing connects those buttons to its owned light group, so a pressed button
has a defined path back to the lights controlled by that crossing. A button that
is already attached to one crossing cannot be reused by another crossing, which
keeps existing crossings from losing their button-to-light-group invariant. The
button getters remain useful for inspecting the linked light group, while
`PedestrianCrossing.getButtonAtStart()` and `getButtonAtEnd()` return `Optional`
views for callers that need to handle crossings without buttons.

## Dependency notes

The current simulator entry point uses only standard Java APIs. JavaFX dependencies
are intentionally not declared until a GUI is added, which keeps Maven dependency
resolution portable across Linux, macOS, and Windows.


## CI/CD workflows

Repository automation uses CI workflows plus GitHub code scanning default setup:

- **CI** (`.github/workflows/ci.yml`) runs `mvn -B verify` on pushes to
  `main` and on pull requests, including forked pull requests. The build/test job
  uploads the packaged jar from `target/*.jar` as the
  `traffic-light-simulator-jar` workflow artifact.
- **SonarQube Cloud analysis** is a separate CI job that waits for the
  fork-safe build/test job, skips fork pull requests, and only runs when
  `SONAR_TOKEN` is available. This keeps external contributor pull requests from
  failing because repository secrets are unavailable.
- **CodeQL** is provided by GitHub code scanning default setup for this
  repository. A custom advanced CodeQL workflow is intentionally not committed,
  because GitHub rejects advanced-configuration SARIF uploads while default setup
  is enabled.
- **Dependency Review** (`.github/workflows/dependency-review.yml`) checks
  pull-request dependency changes before they merge.

## Static analysis

The Maven build pins the SonarQube Cloud scanner plugin version through the
`sonar.maven.plugin.version` property so CI does not use an implicit, changing
scanner version. The build also sets
`sonar.coverage.jacoco.xmlReportPaths` to `target/site/jacoco/jacoco.xml`, so
SonarQube Cloud imports JaCoCo coverage produced during `mvn verify`. Sonar
analysis is skipped by default so pull-request builds do not fail when
`SONAR_TOKEN` is unavailable. Enable analysis explicitly with the `sonarcloud`
Maven profile and a valid token:

```sh
SONAR_TOKEN=<token> mvn -B -Psonarcloud verify sonar:sonar
```

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the
full license text.
