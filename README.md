<p align="center">
  <a href="https://aemvite.dev" target="_blank" rel="noopener noreferrer">
    <img width="180" src="https://aemvite.dev/static/logo-outlined.png" alt="AEM Vite logo">
  </a>
</p>
<br/>
<p align="center">
  <a href="https://travis-ci.com/aem-vite/aem-vite"><img src="https://img.shields.io/travis/com/aem-vite/aem-vite?label=travis-ci" alt="travis ci build"></a>
  <a href="https://github.com/aem-vite/aem-vite/actions/workflows/build-and-test.yml"><img alt="Build and Test" src="https://github.com/aem-vite/aem-vite/actions/workflows/build-and-test.yml/badge.svg"></a>
  <a href="https://github.com/aem-vite/aem-vite/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/aem-vite/aem-vite"></a>
  <a href="https://github.com/aem-vite/aem-vite/pulls"><img alt="GitHub pull requests" src="https://img.shields.io/github/issues-pr/aem-vite/aem-vite"></a>
  <a href="https://mvnrepository.com/artifact/dev.aemvite/aem-vite.all"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.aemvite/aem-vite"></a>
  <a href="https://github.com/aem-vite/aem-vite/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/badge/Licence-Apache%202.0-blue.svg"></a>
  <a href="https://sonarcloud.io/dashboard?id=aem-vite_aem-vite"><img alt="Sonar Quality Gate" src="https://img.shields.io/sonar/quality_gate/aem-vite_aem-vite?server=https%3A%2F%2Fsonarcloud.io"></a>
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
- ui.config: contains runmode specific OSGi configs for the project
- all: a single content package that embeds all the compiled modules (bundles and content packages) including any
  vendor dependencies

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
