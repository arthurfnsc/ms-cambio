package br.com.fiap.mba.corda.contracts

import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.ASSINATURA_OBLITO_OBRIGATORIA
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.ASSINATURA_PROPONENTE_OBRIGATORIA
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.COMPRADOR_NAO_MODIFICADO
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.NENHUM_TIMESTAMP
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.UNICO_INPUT
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.UNICO_OUTPUT
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.VENDEDOR_NAO_MODIFICADO
import br.com.fiap.mba.corda.states.PropostaState
import net.corda.core.identity.CordaX500Name
import net.corda.testing.contracts.DummyState
import net.corda.testing.core.DummyCommandData
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class ContraPropostaContractTest {

    private companion object {

        private const val COMANDO_VALIDO = "Required " +
            "br.com.fiap.mba.corda.contracts.NegociacaoContract.Commands command"
    }

    private val ledgerServices = MockServices(
        listOf("br.com.fiap.mba.corda.contracts")
    )

    private val alice = TestIdentity(CordaX500Name("alice", "New York", "US"))
    private val bob = TestIdentity(CordaX500Name("bob", "Tokyo", "JP"))
    private val charlie = TestIdentity(CordaX500Name("charlie", "London", "GB"))

    private val propostaState1 = PropostaState(
        statusTransacao = "CONTRA-PROPOSTA",
        comprador = alice.party,
        proponente = alice.party,
        vendedor = bob.party,
        oblato = bob.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    private val propostaState2 = PropostaState(
        statusTransacao = "CONTRA-PROPOSTA",
        comprador = alice.party,
        proponente = alice.party,
        vendedor = bob.party,
        oblato = bob.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.ONE
    )

    private val propostaStateInvalida1 = PropostaState(
        statusTransacao = "CONTRA-PROPOSTA",
        comprador = alice.party,
        proponente = alice.party,
        vendedor = charlie.party,
        oblato = charlie.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    private val propostaStateInvalida2 = PropostaState(
        statusTransacao = "CONTRA-PROPOSTA",
        comprador = charlie.party,
        proponente = charlie.party,
        vendedor = bob.party,
        oblato = bob.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    private val propostaStateInvalida3 = PropostaState(
        statusTransacao = "CONTRA-PROPOSTA",
        comprador = bob.party,
        proponente = bob.party,
        vendedor = bob.party,
        oblato = bob.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    @Test
    fun `deve validar se modificações de transações possuem apenas um input e output`() {
        ledgerServices.ledger {
            transaction {
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                tweak {
                    NegociacaoContract.ID?.let { input(it, propostaState1) }
                    NegociacaoContract.ID?.let { input(it, propostaState1) }
                    failsWith(UNICO_INPUT)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaState2) }
                    NegociacaoContract.ID?.let { output(it, propostaState2) }
                    failsWith(UNICO_INPUT)
                }
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                verifies()
            }
        }
    }

    @Test
    fun `deve aceitar modificações de input do tipo PropostaState e output do tipo PropostaState`() {
        ledgerServices.ledger {
            transaction {
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                tweak {
                    NegociacaoContract.ID?.let { input(it, propostaState1) }
                    failsWith(UNICO_OUTPUT)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, DummyState()) }
                    failsWith(UNICO_INPUT)
                }
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                verifies()
            }
        }
    }

    @Test
    fun `deve aceitar modificações de input e output do tipo PropostaState`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                tweak {
                    NegociacaoContract.ID?.let { input(it, propostaState1) }
                    failsWith(UNICO_INPUT)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaState1) }
                    failsWith(UNICO_OUTPUT)
                }
                verifies()
            }
        }
    }

    @Test
    fun `deve possuir uma transação de proposta e ter exatamente um comando do tipo Proposta`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                tweak {
                    command(listOf(alice.publicKey, bob.publicKey), DummyCommandData)
                    failsWith(COMANDO_VALIDO)
                }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                verifies()
            }
        }
    }

    @Test
    fun `deve validar se a transação possui taxas diferentes`() {
        ledgerServices.ledger {
            transaction {
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                tweak {
                    NegociacaoContract.ID?.let { input(it, propostaState1) }
                    NegociacaoContract.ID?.let { output(it, propostaState1) }
                    verifies()
                }
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                verifies()
            }
        }
    }

    @Test
    fun `deve validar se a transação possui duas assinaturas - proponente e oblito`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                tweak {
                    command(listOf(alice.publicKey, charlie.publicKey), NegociacaoContract.Commands.ContraProposta())
                    failsWith(ASSINATURA_OBLITO_OBRIGATORIA)
                }
                tweak {
                    command(listOf(charlie.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                    failsWith(ASSINATURA_PROPONENTE_OBRIGATORIA)
                }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                verifies()
            }
        }
    }

    @Test
    fun `deve validar se comprador e vendedor não alteram o output`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaStateInvalida1) }
                    failsWith(VENDEDOR_NAO_MODIFICADO)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaStateInvalida2) }
                    failsWith(COMPRADOR_NAO_MODIFICADO)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaStateInvalida3) }
                    failsWith(COMPRADOR_NAO_MODIFICADO)
                }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                verifies()
            }
        }
    }

    @Test
    fun `deve aceitar propostas sem timestamp`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { input(it, propostaState1) }
                NegociacaoContract.ID?.let { output(it, propostaState2) }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.ContraProposta())
                tweak {
                    timeWindow(Instant.now())
                    failsWith(NENHUM_TIMESTAMP)
                }
            }
        }
    }
}
