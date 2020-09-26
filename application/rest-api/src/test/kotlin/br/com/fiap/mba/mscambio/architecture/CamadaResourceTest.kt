package br.com.fiap.mba.mscambio.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
import com.tngtech.archunit.core.importer.ImportOptions
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class CamadaResourceTest {

    companion object {

        private const val DIRETORIO_ANALISE = "..resources.impl.."

        private val classes =
            ClassFileImporter(ImportOptions().with(DO_NOT_INCLUDE_TESTS))
                .importPackages("br.com.fiap.mba.mscambio")
    }

    @Test
    fun `nenhuma controller deve acessar classes`() {
        noClasses()
            .that().resideInAPackage(DIRETORIO_ANALISE)
            .should().accessClassesThat()
            .resideInAnyPackage()
            .check(classes)
    }

    @Test
    fun `nenhuma controller deve depender de classes`() {
        noClasses()
            .that().resideInAPackage(DIRETORIO_ANALISE)
            .should().dependOnClassesThat()
            .resideInAnyPackage()
            .check(classes)
    }

    @Test
    fun `uma controller só pode acessar classes`() {
        classes().that().resideInAPackage(DIRETORIO_ANALISE)
            .should().onlyAccessClassesThat()
            .resideInAnyPackage(
                "..converters..",
                "..corda..",
                "..java..",
                "..kotlin..",
                "..openapi..",
                "..resources..",
                "..services..",
                "..springframework.."
            )
            .check(classes)
    }

    @Test
    fun `uma controller só pode depender de classes`() {
        classes().that().resideInAPackage(DIRETORIO_ANALISE)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "..converters..",
                "..corda..",
                "..java..",
                "..jetbrains..",
                "..kotlin..",
                "..openapi..",
                "..services..",
                "..springframework.."
            )
            .check(classes)
    }
}
