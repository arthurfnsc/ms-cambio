# ms-cambio

API utilizada para exemplificar fluxo de projeto com Corda do desafio FIAP  
para cenário de câmbio

## GitHub flow

O projeto utiliza o fluxo de versionamento [GitHub flow](https://guides.github.com/introduction/flow/).

## Estrutura

```console

```

## Arquitetura

- [Gradle](https://gradle.org/)
- [Kotlin](https://kotlinlang.org/)
- [Mapstruct](http://mapstruct.org/)
- [Open API Code Generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin)
- [Spring Boot 2.3](https://projects.spring.io/spring-boot/)

## Execução

### Clone

```console
foo@bar:~$ git clone https://github.com/arthurfnsc/ms-cambio.git
foo@bar:~$ cd ms-cambio
```

### Execução Linux | Windows

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] clean bootRun
--args='--spring.profiles.active=[PROFILE]
```

#### Execução em Profile

O projeto Springboot trabalha com profiles e variáveis.

A variável de port também pode ser configurada. Por default o valor é 8080

## Qualidade de Código

- [Detekt](https://github.com/arturbosch/detekt)
- [JaCoCo](https://www.eclemma.org/jacoco/)
- [Markdown Lint](https://github.com/appmattus/markdown-lint)
- [SonarQube](https://www.sonarqube.org/)

## Relatórios

- [Verificar versão de dependência](https://github.com/ben-manes/gradle-versions-plugin)

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] dependencyUpdates
```

- [Documentação classes](https://github.com/Kotlin/dokka)

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] dokka
```

- [Projeto](https://docs.gradle.org/current/userguide/project_report_plugin.html)

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] projectReport
```

## Security

- [OSS Index](https://github.com/OSSIndex/ossindex-gradle-plugin/)

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] audit
```

- [OWASP](https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html)

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] dependencyCheckAggregate
```
