## [3.0.2](https://github.com/aem-vite/aem-vite/compare/v3.0.1...v3.0.2) (2025-08-31)


### Bug Fixes

* remove unsupported log pattern field ([a2f8d36](https://github.com/aem-vite/aem-vite/commit/a2f8d36f8203807bc5a00480961a5fd874f6ac34))
* update service user with principal names ([df5e422](https://github.com/aem-vite/aem-vite/commit/df5e422761cd82a5d96a82a36a483b3642f4b4fd))

## [3.0.1](https://github.com/aem-vite/aem-vite/compare/v3.0.0...v3.0.1) (2025-08-30)


### Bug Fixes

* resolve build error with service user mapping [#44](https://github.com/aem-vite/aem-vite/issues/44) ([266eb2c](https://github.com/aem-vite/aem-vite/commit/266eb2c37356595b771a3c7191cf28137e4f9b92))

# [3.0.0](https://github.com/aem-vite/aem-vite/compare/v2.3.0...v3.0.0) (2022-12-06)


### Bug Fixes

* resolve unclosed resource resolver ([c146cef](https://github.com/aem-vite/aem-vite/commit/c146cefe742e15e0fb79dd15d653a33a3d80c8dc))

# [2.3.0](https://github.com/aem-vite/aem-vite/compare/v2.2.2...v2.3.0) (2022-07-01)


### Code Refactoring

* remove Vite DevServer functionality ([2675c00](https://github.com/aem-vite/aem-vite/commit/2675c00befaf4a3d124cee8036555782f7e0743f))


### BREAKING CHANGES

* This functionality has been superseded by the vite-aem-plugin npm package

Refer to https://www.aemvite.dev/guide/front-end/vite-plugin/ for instructions on getting the Vite DevServer configuration updated.

## [2.2.2](https://github.com/aem-vite/aem-vite/compare/v2.2.1...v2.2.2) (2022-01-19)


### Bug Fixes

* ensure apps paths don't overlap ([bf6a92c](https://github.com/aem-vite/aem-vite/commit/bf6a92c796731dce82133162d78c95bcffe0cad6))

## [2.2.1](https://github.com/aem-vite/aem-vite/compare/v2.2.0...v2.2.1) (2021-10-28)


### Bug Fixes

* resolve repoinit failing during install ([5feb0c2](https://github.com/aem-vite/aem-vite/commit/5feb0c202f00e9341ed601f3a901c6ae0de768c3))

# [2.2.0](https://github.com/aem-vite/aem-vite/compare/v2.1.5...v2.2.0) (2021-09-27)


### Refactor

* removed ACS Commons dependency ([79fe70f](https://github.com/aem-vite/aem-vite/commit/79fe70fe378eace700a8d72cdef2056ca8395ee2))

## [2.1.5](https://github.com/aem-vite/aem-vite/compare/v2.1.4...v2.1.5) (2021-09-27)


### Bug Fixes

* resolve user mapping for precompiled files ([c8bb838](https://github.com/aem-vite/aem-vite/commit/c8bb8387720496dbb4b5ea1eb625dea213476bf9))

## [2.1.4](https://github.com/aem-vite/aem-vite/compare/v2.1.3...v2.1.4) (2021-09-22)


### Bug Fixes

* switched from principal users to single user mapping ([1c66f00](https://github.com/aem-vite/aem-vite/commit/1c66f008f7aa9655004b568cd8dea3da77b4b0e9))

## [2.1.3](https://github.com/aem-vite/aem-vite/compare/v2.1.2...v2.1.3) (2021-07-30)


### Bug Fixes

* updated `getLibraries` execute to use `transitive` resolution ([33b0edf](https://github.com/aem-vite/aem-vite/commit/33b0edf3fa077dfc0940b7473aee888c51818994))

## [2.1.2](https://github.com/aem-vite/aem-vite/compare/v2.1.1...v2.1.2) (2021-07-25)


### Bug Fixes

* ensure fallback resource resolver is available ([830c660](https://github.com/aem-vite/aem-vite/commit/830c66034de3217fd1dfb27dbd45e2663a056f41))

## [2.1.1](https://github.com/aem-vite/aem-vite/compare/v2.1.0...v2.1.1) (2021-07-11)


### Bug Fixes

* ensure aem-vite-clientlibs user gets installed ([9aa0604](https://github.com/aem-vite/aem-vite/commit/9aa060499cf2c04bcf1e38329ac8c5bcd2986fc5))

# [2.1.0](https://github.com/aem-vite/aem-vite/compare/v2.0.3...v2.1.0) (2021-07-11)


### Features

* added support for JCR resolver access via 'aem-vite-clientlibs' ([acffe47](https://github.com/aem-vite/aem-vite/commit/acffe4787f873559c3460dca6b5f2bb9132be4ff))

## [2.0.3](https://github.com/aem-vite/aem-vite/compare/v2.0.2...v2.0.3) (2021-06-13)


### Bug Fixes

* check if content changes after ClientLib removals ([75b19c4](https://github.com/aem-vite/aem-vite/commit/75b19c4ed38cc692ccf81aa86e7d958bb9a1e65a))
* pages not display due to error handling ([024aaec](https://github.com/aem-vite/aem-vite/commit/024aaec34aff567d51243a33a036f25ce8eaa561))
* prevent extra script/link tags been removed ([a59f698](https://github.com/aem-vite/aem-vite/commit/a59f698614e7d0705ab1594a0911d354d655a49c))

## [2.0.2](https://github.com/aem-vite/aem-vite/compare/v2.0.1...v2.0.2) (2021-06-08)


### Bug Fixes

* resolved issues with the `accepts` logic ([3fca432](https://github.com/aem-vite/aem-vite/commit/3fca43201088cc7a7ff2eea9ea0664a183deca64))

## [2.0.1](https://github.com/aem-vite/aem-vite/compare/v2.0.0...v2.0.1) (2021-06-02)


### Bug Fixes

* resolved some ClientLib paths not being removed ([f19abd0](https://github.com/aem-vite/aem-vite/commit/f19abd0d52b270c04fc74ab56a698da151e4a398))

# [2.0.0](https://github.com/aem-vite/aem-vite/compare/v1.0.2...v2.0.0) (2021-06-01)


### Features

* **core:** replace AEM entry with ES6 module entry ([9d29c48](https://github.com/aem-vite/aem-vite/commit/9d29c4896e23275ad62f74f8060f8b055a5996a3))


### BREAKING CHANGES

* **core:** New ClientLib handling for js.txt

The js.txt file now requires the entry file path rather than the conventional AEM `#base` statement and file names/paths. See the below example.
```
resources/js/main.js
```

Please note that only a single entry is supported at this time with the possibility of multiple coming in the near future.

## [1.0.2](https://github.com/aem-vite/aem-vite/compare/v1.0.1...v1.0.2) (2021-05-19)


### Bug Fixes

* resolved react refresh crashing pages ([877a868](https://github.com/aem-vite/aem-vite/commit/877a868659df15b3c11b8e91f559190d8489cb4b))

## [1.0.1](https://github.com/aem-vite/aem-vite/compare/v1.0.0...v1.0.1) (2021-05-19)


### Bug Fixes

* add missing `@Override` annotation ([ee535dc](https://github.com/aem-vite/aem-vite/commit/ee535dc1213d309d3f9958239942972d9ae8d14a))
* resolved possible race condition with `devServerConfigurations` ([aa9fe31](https://github.com/aem-vite/aem-vite/commit/aa9fe31bf9c8bee1a05c62b8e01bfbb0a99d3523))
* switched from obsolete `List` instance type ([e9d6b35](https://github.com/aem-vite/aem-vite/commit/e9d6b35836510dcc33f520a43e2e3a1bc01b8dac))

## 1.0.0 (2021-05-19)


# Initial Public Release 🎉 🎉

After much fun getting GPG signing and CI pipelines working, I'm happy to announce the first (but early) version of AEM Vite.

Visit https://aemvite.dev/ for more information.
