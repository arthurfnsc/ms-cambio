package br.com.fiap.mba.mscambio.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

@AnalyzeClasses(packages = ["br.com.fiap.mba.mscambio"])
class InterfacesTest {

    @ArchTest
    private fun `interfaces não devem estar no diretório impl`(classes: JavaClasses) {
        noClasses()
            .that().resideInAPackage("..impl..")
            .should().beInterfaces()
            .check(classes)
    }

    @ArchTest
    private fun `interfaces não devem ter Interface no nome`(classes: JavaClasses) {
        noClasses()
            .that().areInterfaces()
            .should().haveSimpleNameContaining("Interface")
            .check(classes)
    }
}
