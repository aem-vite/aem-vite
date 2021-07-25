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
