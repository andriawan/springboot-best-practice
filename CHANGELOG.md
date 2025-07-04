# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

## [1.4.0](https://github.com/andriawan/springboot-best-practice/compare/v1.3.0...v1.4.0) (2025-07-02)


### Features

* add roles to user entity, auth, mapper and response ([60de33c](https://github.com/andriawan/springboot-best-practice/commit/60de33cd5b0c487127da28a2ea9133c835b50136))
* **scope:** add scope to JWT token based on user roles ([ae6f2a4](https://github.com/andriawan/springboot-best-practice/commit/ae6f2a4efbae0941156240a1e8137dbc7dde63db))

## [1.3.0](https://github.com/andriawan/springboot-best-practice/compare/v1.2.1...v1.3.0) (2025-06-27)


### Features

* add handler response controller for exception ([ea63a05](https://github.com/andriawan/springboot-best-practice/commit/ea63a0519fe0a0520f163b9046c11021b22bef16))

### [1.2.1](https://github.com/andriawan/springboot-best-practice/compare/v1.2.0...v1.2.1) (2025-06-27)


### Bug Fixes

* fix bug token generation ([48eae1e](https://github.com/andriawan/springboot-best-practice/commit/48eae1ebf069f1148c120ed372c902d6f07c5b0e))

## [1.2.0](https://github.com/andriawan/springboot-best-practice/compare/v1.1.0...v1.2.0) (2025-06-23)


### Features

* implement base rate limit with jcache (ehcache) and bucket4j ([5017e5c](https://github.com/andriawan/springboot-best-practice/commit/5017e5cf4531188229fca04f7a49d36dc8f45971))

## [1.1.0](https://github.com/andriawan/springboot-best-practice/compare/v1.0.1...v1.1.0) (2025-06-22)


### Features

* docker configuration for app production deployment ([1585248](https://github.com/andriawan/springboot-best-practice/commit/1585248a8c83a408c0f13d2c2af82fe832bfd79e))

### [1.0.1](https://github.com/andriawan/springboot-best-practice/compare/v1.0.0...v1.0.1) (2025-06-22)


### Bug Fixes

* delete existing token before issuing refresh token ([046a3d1](https://github.com/andriawan/springboot-best-practice/commit/046a3d1792645c6d4438194df804a8a8b734f1b3))
* make Bad Credential exception return status code 400 ([8796c00](https://github.com/andriawan/springboot-best-practice/commit/8796c00bd8d289a70c9b7dd6032d3ff9b6d8f412))

## 1.0.0 (2025-06-17)


### Features

* activate cors. make it configurable via properties ([2497064](https://github.com/andriawan/springboot-best-practice/commit/249706431972230423cc9e10449be53a8c06cdf6))
* **encoder:** add delegate password encoder ([94850cc](https://github.com/andriawan/springboot-best-practice/commit/94850cc1fe11bd2350abc74ced90219a38d81a29))
* **endpoint:** add new endpoint /me ([c200c0f](https://github.com/andriawan/springboot-best-practice/commit/c200c0fc8c73e0bff26c6b7026acd1fc630fd838))
* **execption-handling:** make auth exception as json response ([dc3a003](https://github.com/andriawan/springboot-best-practice/commit/dc3a003a46f909d810f332cd6578590781246f54))
* **git-info:** map git properties to config class ([e4b1ff5](https://github.com/andriawan/springboot-best-practice/commit/e4b1ff5e925df1faf674a8483ec539e529022202))
* **init:** init repo ([954b483](https://github.com/andriawan/springboot-best-practice/commit/954b48315d62d54b2b4c11786acb1ccc48e58174))
* **jpa-userdetails:** change in-memory user details to jpa based user details" ([1ecae51](https://github.com/andriawan/springboot-best-practice/commit/1ecae511cb70194247f6a50631794bb18a6bafab))
* **jwt:** config jwt based auth ([b75e536](https://github.com/andriawan/springboot-best-practice/commit/b75e5368c4102fbaff08c0b2e8efa4aa985ea1fa))
* **password:** hide password from response ([3e79044](https://github.com/andriawan/springboot-best-practice/commit/3e79044b17ba05ed0576ee75a5e2bb8a8a6d78e6))
* **readme:** add readme project ([f243c31](https://github.com/andriawan/springboot-best-practice/commit/f243c317b0828e81ac7009413f2375e3b17d2c54))
* **refresh-token:** implement refresh token with blacklist mechanism ([213b008](https://github.com/andriawan/springboot-best-practice/commit/213b008396529cf08574a6c6f23614f8af98237c))
* **swagger:** implement swagger ui ([c06a5ff](https://github.com/andriawan/springboot-best-practice/commit/c06a5ff73b7cba2c97438ce4f8edbef952203510))
* **user:** add user basic feature ([a9d11ba](https://github.com/andriawan/springboot-best-practice/commit/a9d11ba3318e8658f3d51b85528543f72ec20de9))
