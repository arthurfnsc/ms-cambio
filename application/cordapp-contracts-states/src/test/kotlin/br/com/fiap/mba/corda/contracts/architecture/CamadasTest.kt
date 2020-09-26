package br.com.fiap.mba.corda.contracts.architecture

import br.com.fiap.mba.corda.contracts.NegociacaoContract
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.Contract
import net.corda.core.contracts.LinearState

@AnalyzeClasses(packages = ["br.com.fiap.mba.corda.contracts"])
class CamadasTest {

    @ArchTest
    private fun `classes Contract devem estar no diretório contracts`(classes: JavaClasses) {
        classes()
            .that().haveSimpleNameEndingWith("Contract")
            .should().dependOnClassesThat().areAssignableTo(Contract::class.java)
            .andShould().resideInAPackage("..contracts..")
            .`as`("classes Contract devem estar no diretório 'contracts'")
            .check(classes)
    }

    @ArchTest
    private fun `classes State devem estar no diretório states`(classes: JavaClasses) {
        classes()
            .that().haveSimpleNameEndingWith("State")
            .should().beAnnotatedWith(BelongsToContract::class.java)
            .andShould().dependOnClassesThat().areAssignableTo(LinearState::class.java)
            .andShould().dependOnClassesThat().areAssignableTo(NegociacaoContract::class.java)
            .andShould().resideInAPackage("..states..")
            .`as`("classes States devem estar no diretório 'states'")
            .check(classes)
    }
}
