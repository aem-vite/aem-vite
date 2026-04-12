<p align="center">
  <a href="https://aemvite.dev" target="_blank" rel="noopener noreferrer">
    <img width="180" src="https://aemvite.dev/static/logo-outlined.png" alt="AEM Vite logo">
  </a>
</p>
<br/>
<p align="center">
  <a href="https://mvnrepository.com/artifact/dev.aemvite/aem-vite.all"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.aemvite/aem-vite"></a>
  <a href="https://github.com/aem-vite/aem-vite/actions/workflows/ci.yml"><img alt="Build and Test" src="https://github.com/aem-vite/aem-vite/actions/workflows/ci.yml/badge.svg?branch=develop"></a>
  <a href="https://github.com/aem-vite/aem-vite/actions/workflows/ci.yml"><img alt="Sonarcloud" src="https://github.com/aem-vite/aem-vite/actions/workflows/ci.yml/badge.svg?branch=develop"></a>
  <a href="https://sonarcloud.io/dashboard?id=aem-vite_aem-vite"><img alt="Sonar Quality Gate" src="https://sonarcloud.io/api/project_badges/measure?project=aem-vite_aem-vite&metric=alert_status"></a>
  <a href="https://sonarcloud.io/summary/new_code?id=aem-vite_aem-vite"><img alt="Sonar Coverage" src="https://sonarcloud.io/api/project_badges/measure?project=aem-vite_aem-vite&metric=coverage"></a>
  <a href="https://sonarcloud.io/dashboard?id=aem-vite_aem-vite"><img alt="Sonar Security Rating" src="https://sonarcloud.io/api/project_badges/measure?project=aem-vite_aem-vite&metric=security_rating"></a>
  <a href="https://github.com/aem-vite/aem-vite/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/aem-vite/aem-vite"></a>
  <a href="https://github.com/aem-vite/aem-vite/pulls"><img alt="GitHub pull requests" src="https://img.shields.io/github/issues-pr/aem-vite/aem-vite"></a>
  <a href="https://github.com/aem-vite/aem-vite/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/badge/Licence-Apache%202.0-blue.svg"></a>
</p>
<br/>

# AEM Vite

AEM Vite is a transparent handler for the next generation front end tool, [Vite](https://vitejs.dev/). Tooling can
be the most complex piece of a project to complete, Vite for AEM aims to bring speed and simplicity without needing to
invest hours of your time upfront.

## Getting Started

Visit [aemvite.dev](https://aemvite.dev) for install instructions.

## Modules

The main parts are:

- core: Java bundle containing all core functionality
- ui.apps: contains the /apps parts of the project
- ui.config: contains runmode specific OSGi configs for the legacy (AEM 6.5) output
- ui.config.cloud: contains runmode specific OSGi configs for the cloud (AEMaaCS) output
- all: legacy (AEM 6.5) content package embedding compiled modules
- all.cloud: cloud (AEMaaCS) content package embedding compiled modules

## Release lanes and artifact mapping

- Legacy lane (unchanged):
  - `dev.aemvite:aem-vite.core`
  - `dev.aemvite:aem-vite.ui.apps`
  - `dev.aemvite:aem-vite.ui.apps.structure`
  - `dev.aemvite:aem-vite.ui.config`
  - `dev.aemvite:aem-vite.all`
- Cloud lane (new):
  - `dev.aemvite:aem-vite.ui.config.cloud`
  - `dev.aemvite:aem-vite.all.cloud`

Existing AEM 6.5 consumers can stay on current artifacts. AEMaaCS consumers can switch to the new `.cloud` artifacts.

## Release Maven profile intent

- `central-publish`: enables signing/staging/publish behavior for Maven Central release flows
- `cloud`: selects the AEM Cloud Service dependency/version set
- `central-publish,cloud`: publishes cloud-targeted artifacts to Maven Central
- `ci-legacy`: lane-isolated module set for legacy packaging/testing (`core`, `ui.apps`, `ui.apps.structure`, `ui.config`, `all`)
- `ci-cloud`: lane-isolated module set for cloud packaging/testing (`core`, `ui.apps`, `ui.apps.structure`, `ui.config.cloud`, `all.cloud`)

## Java support matrix

- Build compatibility: Java 8 bytecode (`source`/`target` 1.8)
- Minimum required Java runtime for Maven execution: Java 8+
- CI coverage:
  - Legacy profile: JDK 8, JDK 11
  - Cloud profile: JDK 17, JDK 21
- Release and release dry-run workflows run on JDK 21

## SNAPSHOT publishing

- Workflow: `.github/workflows/snapshot.yml`
- Triggers: push to `canary` and manual `workflow_dispatch`
- Target repositories:
  - GitHub Packages (`github-packages` Maven profile)
  - Sonatype Central Portal snapshots (`central-snapshots` Maven profile)
- Scope:
  - GitHub publish command uses `github-packages,cloud,ci-cloud`
  - Sonatype publish command uses `central-snapshots,cloud,ci-cloud`
- Sonatype snapshot endpoint: `https://central.sonatype.com/repository/maven-snapshots/`
- Prerequisite: enable snapshots for the `dev.aemvite` namespace in Sonatype Central Portal before running the snapshot workflow
- Isolation: this workflow only publishes `*-SNAPSHOT` versions and is separate from Maven Central GA release workflows

## Split strategy guidance

- Keep shared modules (`core`, `ui.apps`, `ui.apps.structure`) unified across lanes.
- Keep lane-specific modules split only where they differ (`ui.config`/`ui.config.cloud`, `all`/`all.cloud`).
- Revisit deeper lane splitting only when divergence is proven (different Java code, different app content, or incompatible dependency baselines).

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the
following command:

    mvn clean install -PautoInstallSinglePackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish

Or alternatively

    mvn clean install -PautoInstallSinglePackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

Or to deploy only a single content package, run in the sub-module directory (i.e `ui.apps`)

    mvn clean install -PautoInstallPackage

## Unit tests

This show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

## Static Analysis

The `analyse` module performs static analysis on the project for deploying into AEMaaCS. It is automatically run when
executing

    mvn clean install

from the project root directory. Additional information about this analysis and how to further configure it can be found
here https://github.com/adobe/aemanalyser-maven-plugin

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html

## Contributing

Before committing any changes please ensure you run `yarn install` and follow the [commit convention](./.github/commit-convention.md)
