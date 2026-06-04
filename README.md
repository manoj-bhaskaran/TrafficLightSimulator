# Traffic Light Simulator

Traffic Light Simulator is a Java 17 project for modeling traffic light
simulation domain objects.

## Project metadata

- **Maven group ID:** `com.trafficlightsimulator`
- **Maven artifact ID:** `TrafficLightSimulator`
- **Current development version:** `0.1.2-SNAPSHOT`
- **Java baseline:** Java 17

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

## Running the application

Run the simulator entry point directly with Maven:

```sh
mvn exec:java
```

Build the executable jar package, then launch it with `java -jar`:

```sh
mvn -B package
java -jar target/TrafficLightSimulator-0.1.2-SNAPSHOT.jar
```

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

## Dependency notes

The current simulator entry point uses only standard Java APIs. JavaFX dependencies
are intentionally not declared until a GUI is added, which keeps Maven dependency
resolution portable across Linux, macOS, and Windows.

## Static analysis

The Maven build pins the SonarQube Cloud scanner plugin version through the
`sonar.maven.plugin.version` property so CI does not use an implicit, changing
scanner version. Sonar analysis is skipped by default so pull-request builds do
not fail when `SONAR_TOKEN` is unavailable. Enable analysis explicitly with the
`sonarcloud` Maven profile and a valid token:

```sh
SONAR_TOKEN=<token> mvn -B -Psonarcloud sonar:sonar
```

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the
full license text.
