package br.com.fiap.mba.mscambio.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS
import com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS
import com.tngtech.archunit.library.GeneralCodingRules.USE_JAVA_UTIL_LOGGING
import com.tngtech.archunit.library.GeneralCodingRules.USE_JODATIME
import org.slf4j.Logger

@AnalyzeClasses(packages = ["br.com.fiap.mba.mscambio"])
class CodificacaoTest {

    @ArchTest
    private fun `classes não devem lançar exceção genérica`(classes: JavaClasses) {
        noClasses().should(THROW_GENERIC_EXCEPTIONS).check(classes)
    }

    @ArchTest
    private fun `classes não devem utilizar o console para escrever informação`(classes: JavaClasses) {
        noClasses().should(ACCESS_STANDARD_STREAMS).check(classes)
    }

    @ArchTest
    private fun `classes não devem utilizar o java_util_logging_Logger`(classes: JavaClasses) {
        noClasses().should(USE_JAVA_UTIL_LOGGING).check(classes)
    }

    @ArchTest
    private fun `classes não devem utilizar o JodaTime`(classes: JavaClasses) {
        noClasses().should(USE_JODATIME).check(classes)
    }

    @ArchTest
    private fun `Loggers devem ser static e final`(classes: JavaClasses) {

        fields().that().haveRawType(Logger::class.java)
            .should().beStatic()
            .andShould().beFinal()
            .because("Loggers devem ser static e final").check(classes)
    }
}
