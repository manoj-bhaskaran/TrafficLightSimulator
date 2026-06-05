# Contributing to Traffic Light Simulator

Thank you for helping improve Traffic Light Simulator. This repository is a pre-MVP Java 17 Maven project, so contributor changes should keep the codebase easy to validate and the project history easy to understand.

## Before you start

1. Open or find a GitHub issue that describes the change.
2. Comment on the issue if you plan a larger change, especially one that may affect public APIs, validation behavior, CI, or release versioning.
3. Create a focused branch from the latest `main`.

## Branch naming

Use short, descriptive, lowercase branch names with hyphen-separated words:

- `feat/<issue-number>-short-description` for new capabilities.
- `fix/<issue-number>-short-description` for bug fixes.
- `docs/<issue-number>-short-description` for documentation-only updates.
- `ci/<issue-number>-short-description` for GitHub Actions or automation changes.
- `chore/<issue-number>-short-description` for repository maintenance.

Examples:

- `feat/42-pedestrian-timing-rules`
- `ci/51-repository-governance`
- `docs/51-contributing-guide`

## Commit messages

Use Commitizen-style conventional commit messages:

```text
<type>(optional-scope): <short imperative summary>
```

Common types include `feat`, `fix`, `docs`, `test`, `ci`, `chore`, and `refactor`. Reference the issue in the commit body or pull-request description when applicable.

Examples:

```text
ci: add dependabot coverage for github actions

docs: document contribution workflow
```

## Development setup

- Install Java 17.
- Install Maven.
- Run commands from the repository root.

Build and test the project with:

```sh
mvn -B verify
```

Generate Javadoc when API documentation changes:

```sh
mvn javadoc:javadoc
```

## Tests and CI expectations

Every pull request should either pass the Maven verification lifecycle or explain why it could not be run locally. The GitHub Actions CI workflow is expected to run on pull requests and `main` pushes, using Java 17 and `mvn -B verify`.

For code changes, add or update tests that cover the changed behavior. For documentation, template, or workflow-only changes, make sure the relevant Markdown/YAML files are reviewed for clarity and syntax.

## CHANGELOG and SemVer expectations

This project follows Semantic Versioning and is currently pre-MVP in the `0.x.y` range. Until `1.0.0`, minor releases (`0.MINOR.0`) may include breaking changes as the domain model evolves; patch releases (`0.x.PATCH`) are reserved for backward-compatible fixes within the current pre-MVP baseline.

When opening a pull request:

- Add a concise entry to `CHANGELOG.md` under `[Unreleased]` for user-visible code, documentation, CI, governance, or workflow changes.
- Update the development version in `pom.xml` and README when the issue or release plan calls for a version increment.
- Use SemVer to choose the next version. For pre-MVP feature, governance, CI, or documentation baseline changes, increment the minor version and keep the `-SNAPSHOT` qualifier during development.
- Mention any breaking behavior or migration notes in the pull request.

## Pull requests

Pull requests should be small enough to review comfortably and should use the repository pull-request template. Include:

- A summary of the change.
- The related issue number.
- Test commands and results.
- Documentation, changelog, and versioning notes.
- Any required maintainer follow-up or manual action.

## Recommended `main` branch protection

Repository administrators should protect `main` in GitHub settings. Recommended rules:

- Require a pull request before merging.
- Require at least one approving review.
- Require status checks to pass before merging, including the CI build/test check.
- Require branches to be up to date before merging when practical.
- Restrict force pushes and branch deletion.
- Require conversation resolution before merging.

Applying these settings requires repository administrator access. Contributors should note in the pull request if a change depends on branch-protection updates or other maintainer-only configuration.
