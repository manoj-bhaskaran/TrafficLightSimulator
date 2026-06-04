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

- Established a Keep a Changelog file with an `Unreleased` section.
- Added MIT licensing documentation for the repository.
- Documented the pre-MVP SemVer versioning policy in the README.
- Documented how to run SonarQube Cloud analysis with an explicit token.

### Changed

- Updated Maven coordinates from the placeholder `com:TrafficLightSimulator` at
  `1.0-SNAPSHOT` to `com.trafficlightsimulator:TrafficLightSimulator` at the
  pre-MVP development baseline `0.1.0-SNAPSHOT`.
- Pinned the SonarQube Cloud Maven scanner plugin version to avoid implicit
  scanner version changes during analysis.

## [0.1.0] - 2026-06-04

### Added

- Established the initial pre-MVP SemVer baseline for future project releases.

[Unreleased]: https://github.com/manoj-bhaskaran/TrafficLightSimulator/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/manoj-bhaskaran/TrafficLightSimulator/releases/tag/v0.1.0
