package br.com.fiap.mba.corda.flows.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

@AnalyzeClasses(packages = ["br.com.fiap.mba.corda.flows"])
class CamadasTest {

    @ArchTest
    private fun `classes Flow devem estar no diretório flows`(classes: JavaClasses) {

        classes()
            .that().haveSimpleNameEndingWith("Flow")
            .should().resideInAPackage("..flows..")
            .`as`("classes Flow devem estar no diretório 'flows'")
            .check(classes)
    }
}
