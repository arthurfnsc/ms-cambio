# ms-cambio

API utilizada para exemplificar fluxo de projeto com Corda do desafio FIAP  
para cenário de câmbio

## GitHub flow

O projeto utiliza o fluxo de versionamento [GitHub flow](https://guides.github.com/introduction/flow/).

## Pré requisitos

### Java 8

<details><summary><b>Instruções</b></summary>

Apesar de o Java já estar em uma versão a frente, o Corda possui uma
limitação sendo necessário utilizar a versão 8 para a execução do [CorDapp](https://docs.corda.net/docs/corda-os/4.5/getting-set-up.html)

> Corda requires at least version 8u171, but do not currently support Java 9  
>or higher for this version of Corda.  
>
>Corda has been tested against the following Java builds:

- Amazon Corretto
- Oracle JDK
- Red Hat’s OpenJDK
- Zulu’s OpenJDK

> OpenJDK builds often exclude JavaFX, which is required by the Corda GUI  
>tools. Corda supports only Java 8.

O Java 8 pode tanto ser instalado através da JDK contida no site
da [Oracle](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
ou no site do [OpenJDK](https://openjdk.java.net/install/)

Como alternativa é possível utilizar o [SDKMan](https://sdkman.io/)
e instalar o Java através do comando:

```console
foo@bar:~$ sdk install java <version>
```

Para listagem de todas as versões do Java disponíveis, execute o comando:

```console
foo@bar:~$ sdk list java
```

</details>

### Gradle (opcional)

<details><summary><b>Instruções</b></summary>

O projeto foi concebido para que a instalação do Gradle fosse opcional,
para tanto, é possível rodar as configurações do projeto após instalação
do Java pelos arquivos **gradle.bat** em sistemas Windows e **gradlew**
 em sistemas Unix, que interagem com o arquivo **gradle-wrapper.jar**
 contido na pasta **gradle/wrapper** na raiz do projeto.

Caso mesmo assim se deseje rodar o projeto pelo Gradle na máquina,
o mesmo pode ser instalado através do [site](https://gradle.org/install/).

Como alternativa é possível utilizar o [SDKMan](https://sdkman.io/)
e instalar o Maven através do comando:

```console
foo@bar:~$ sdk install gradle
```

Para listagem de todas as versões do Gradle disponíveis, execute o comando:

```console
foo@bar:~$ sdk list gradle
```

</details>

## Estrutura

```console

```

## Arquitetura

- [Gradle](https://gradle.org/)
- [Kotlin](https://kotlinlang.org/)
- [Mapstruct](http://mapstruct.org/)
- [Open API 3.0](https://swagger.io/specification/)
- [Open API Code Generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin)
- [Spring Boot 2.3](https://projects.spring.io/spring-boot/)

## Execução

### Clone

```console
foo@bar:~$ git clone https://github.com/arthurfnsc/ms-cambio.git
foo@bar:~$ cd ms-cambio
```

### Execução Linux | Windows

O projeto pode ser executado em ambiente Linux ou Windows, sendo os comandos
diferenciando por duas opções Linux e Windows respectivamente

Primeiramente é preciso gerar os nodes do Corda e subí-los. Essas  
configurações se encontram no arquivo **build.gradle**
dentro da task **deployNodes**

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] clean deployNodes
foo@bar:ms-cambio$ [./build/nodes/runnodes | .\build\nodes\runnodes.bat]
```

Em seguida é preciso subir os servidores com a aplicação Web para teste  
para isso, basta executar as seguintes tasks também presentes no  
arquivo **build.gradle**

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] \
application:rest-api:runBancoServer
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] \
application:rest-api:runCorretoraServer
```

Como o propósito do desafio foi realizar uma Prova de Conceito,  
alguns conteúdos mais avançados de segurança não foram explorados,  
para tanto, serão executadas as soluções como se estivessem em servidores  
Web distintos, porém, as duas apontam para o mesmo ambiente **Corda**:

Em [localhost:50001](http://localhost:50001/mscambio/swagger-ui/) estará a
solução apontando para o usuário do **Banco** e em
[localhost:50002](http://localhost:50002/mscambio/swagger-ui/) a solução
apontando para para o usuário da **Corretora**

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

## Principais problemas

### Erros ao importar projeto em IDE

É comum ao importar o projeto em uma IDE que classes contidas nos
pacotes **org.openapi.cambio.server** apresentem erros de compilação.

Isso ocorre porque ao utilizar a estratégia de API First é necessário
a geração das classes para que o projeto possa compilar.

A geração das classes se encontra atrelada ao goal **compileKotlin** no
ciclo de vida do **Gradle**, e pode ser melhor detalhado em  
**"plugins/openapi.gradle** na raiz do projeto.

```groovy
compileKotlin.dependsOn(
    generateCambioApiServer
)

sourceSets.main.java.srcDir "$cambioApiServerOutput/src/main/java"
```

![Gradle Lifecycle](/imgs/lifecycle.png)

Uma das formas de resolver o problema é a execução da task **compileKotlin**

```console
foo@bar:ms-cambio$ [./gradlew | gradlew.bat] compileKotlin
```

Ou a execução de outras tasks que tenham relação direta com o **compileKotlin**,
como **build** ou **bootRun**.

As classes geradas se encontrarão no diretório **org.openapi.cambio.server**:

```console
.
└── build
    └── generated
        └── openapi-code-server
            ├── pom.xml
            ├── README.md
            └── src
                └── main
                    └── java
                        └── org
                            └── openapi
                                └── cambio
                                    └── server
                                        ├── api
                                        └── model
```

Mesmo após a geração de classes, é comum algumas IDEs ainda não sincronizarem
as novas classes no projeto aberto, para tanto, lembre-se de sincronizar o  
projeto para que as novas classes entrem no classpath do projeto, e com isso,  
possam ser importadas por outras classes.
