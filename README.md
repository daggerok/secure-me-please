secure-me-please [![build](https://travis-ci.org/daggerok/secure-me-please.svg?branch=master)](https://travis-ci.org/daggerok/secure-me-please)
================

spring oauth2 security example (spring-data mongodb users)

spring-stack:
  - spring-security-oauth2
    - resource server
    - authorization server
    - spring-security-test
  - mvc-test
  - spring-data rest/mongo

```sh
git clone ... && cd $_
gradle clean test
```

read for details:
  - comments in `com.daggerok.oauth2.SecureMePleaseApplication` main class
  - or tests in `com.daggerok.oauth2.SecureMePleaseApplicationTests`

links:
  - [spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/)
  - [spring-security-oauth](http://projects.spring.io/spring-security-oauth/docs/Home.html)
  - [spring-data-rest](http://docs.spring.io/spring-data/rest/docs/2.5.1.RELEASE/reference/html/)
  - [spring-data-mongo](http://docs.spring.io/spring-data/data-mongo/docs/current/reference/html/)
  - [embedded mongo](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo)
