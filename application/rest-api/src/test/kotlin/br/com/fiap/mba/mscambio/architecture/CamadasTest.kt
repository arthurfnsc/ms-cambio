package br.com.fiap.mba.mscambio.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.mapstruct.Mapper
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packages = ["br.com.fiap.mba.mscambio"])
class CamadasTest {

    @ArchTest
    private fun `classes Config devem estar no diretório configs`(classes: JavaClasses) {

        classes()
            .that().haveSimpleNameEndingWith("Config")
            .should().resideInAPackage("..configs..")
            .andShould().beAnnotatedWith(Configuration::class.java)
            .`as`("classes Config devem estar no diretório 'configs'")
            .check(classes)
    }

    @ArchTest
    private fun `classes Converter devem estar no diretório converters`(classes: JavaClasses) {

        classes()
            .that().haveSimpleNameEndingWith("Converter")
            .should().resideInAPackage("..converters..")
            .andShould().beAnnotatedWith(Mapper::class.java)
            .`as`("classes Converter devem estar no diretório 'converters'")
            .check(classes)
    }

    @ArchTest
    private fun `classes DTO devem estar no diretório dtos`(classes: JavaClasses) {

        classes()
            .that().haveSimpleNameEndingWith("DTO")
            .should().resideInAPackage("..dtos..")
            .`as`("classes DTO devem estar no diretório 'dtos'")
            .check(classes)
    }

    @ArchTest
    private fun `classes Exception devem estar no diretório exceptions`(classes: JavaClasses) {

        classes()
            .that().haveSimpleNameEndingWith("Exception")
            .should().resideInAPackage("..exceptions..")
            .andShould().dependOnClassesThat().areAssignableTo(RuntimeException::class.java)
            .`as`("classes Exception devem estar no diretório 'exceptions'")
            .check(classes)
    }

    @ArchTest
    private fun `classes Factory devem estar no diretório factories`(classes: JavaClasses) {

        classes()
            .that().haveSimpleNameEndingWith("Factory")
            .should().resideInAPackage("..factories..")
            .`as`("classes Factory devem estar no diretório 'factories'")
            .check(classes)
    }

    @ArchTest
    private fun `classes ResourceImpl devem estar no diretório resources_impl`(classes: JavaClasses) {
        classes()
            .that().haveSimpleNameEndingWith("ResourceImpl")
            .should().beAnnotatedWith(RestController::class.java)
            .andShould().resideInAPackage("..resources.impl..")
            .`as`("classes ResourceImpl devem estar no diretório 'resources.impl'")
            .check(classes)
    }

    @ArchTest
    private fun `interfaces Service devem estar no diretório services`(classes: JavaClasses) {
        classes()
            .that().haveSimpleNameEndingWith("Service")
            .should().beInterfaces()
            .andShould().resideInAPackage("..services..")
            .`as`("interfaces Service devem estar no diretório 'services'")
            .check(classes)
    }

    @ArchTest
    private fun `classes ServiceImpl devem estar no diretório services_impl`(classes: JavaClasses) {
        classes()
            .that().haveNameMatching(".*ServiceImpl")
            .should().beAnnotatedWith(Service::class.java)
            .andShould().resideInAPackage("..services.impl..")
            .`as`("classes ServiceImpl devem estar no diretório 'services.impl'")
            .check(classes)
    }
}
