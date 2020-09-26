package br.com.fiap.mba.mscambio.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
import com.tngtech.archunit.core.importer.ImportOptions
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArquiteturaCamadasTest {

    companion object {

        private const val BASE_PATH = "br.com.fiap.mba.mscambio"

        private val classes =
            ClassFileImporter(ImportOptions().with(DO_NOT_INCLUDE_TESTS))
                .importPackages(BASE_PATH)
    }

    @Test
    fun `camadas devem seguir especificação`() {

        layeredArchitecture()
            .layer("Resources").definedBy("${BASE_PATH}.resources.impl..")
            .layer("Services").definedBy("${BASE_PATH}..services.impl..")

            .whereLayer("Resources").mayNotBeAccessedByAnyLayer()
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Resources")
            .check(classes)
    }
}
