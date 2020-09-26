package br.com.fiap.mba.corda.contracts

import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.ASSINATURA_OBLITO_OBRIGATORIA
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.ASSINATURA_PROPONENTE_OBRIGATORIA
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.COMPRADOR_VENDEDOR
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.NENHUM_INPUT
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.NENHUM_TIMESTAMP
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.UNICO_OUTPUT
import br.com.fiap.mba.corda.contracts.NegociacaoContract.Companion.UNICO_OUTPUT_PROPOSTASTATE
import br.com.fiap.mba.corda.states.PropostaState
import net.corda.core.identity.CordaX500Name
import net.corda.testing.contracts.DummyContract
import net.corda.testing.contracts.DummyState
import net.corda.testing.core.DummyCommandData
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class PropostaContractTest {

    private companion object {

        private const val COMANDO_VALIDO = "Required " +
            "br.com.fiap.mba.corda.contracts.NegociacaoContract.Commands command"
        private const val INPUT_OUTPUT_STATE = "A transaction must contain at least one input or output state"
    }

    private val ledgerServices = MockServices(
        listOf("br.com.fiap.mba.corda.contracts", "net.corda.testing.contracts")
    )

    private val alice = TestIdentity(CordaX500Name("alice", "New York", "US"))
    private val bob = TestIdentity(CordaX500Name("bob", "Tokyo", "JP"))
    private val charlie = TestIdentity(CordaX500Name("charlie", "London", "GB"))

    private val propostaState = PropostaState(
        statusTransacao = "ABERTO",
        comprador = alice.party,
        proponente = alice.party,
        vendedor = bob.party,
        oblato = bob.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    private val propostaStateInvalida1 = PropostaState(
        statusTransacao = "ABERTO",
        comprador = alice.party,
        proponente = bob.party,
        vendedor = bob.party,
        oblato = alice.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    private val propostaStateInvalida2 = PropostaState(
        statusTransacao = "ABERTO",
        comprador = alice.party,
        proponente = alice.party,
        vendedor = bob.party,
        oblato = charlie.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    private val propostaStateInvalida3 = PropostaState(
        statusTransacao = "ABERTO",
        comprador = alice.party,
        proponente = charlie.party,
        vendedor = bob.party,
        oblato = bob.party,
        moeda = "USD",
        quantidade = 1,
        cotacaoReal = BigDecimal.ONE,
        taxa = BigDecimal.TEN
    )

    @Test
    fun `deve possuir um output do tipo PropostaState`() {
        ledgerServices.ledger {
            transaction {
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.Proposta())
                failsWith(INPUT_OUTPUT_STATE)
                tweak {
                    NegociacaoContract.ID?.let { output(it, DummyState()) }
                    failsWith(UNICO_OUTPUT_PROPOSTASTATE)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaState) }
                    NegociacaoContract.ID?.let { output(it, propostaState) }
                    failsWith(UNICO_OUTPUT)
                }
                NegociacaoContract.ID?.let { output(it, propostaState) }
                verifies()
            }
        }
    }

    @Test
    fun `deve possuir uma transação de proposta e ter exatamente um comando do tipo Proposta`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { output(it, propostaState) }
                tweak {
                    command(listOf(alice.publicKey, bob.publicKey), DummyCommandData)
                    failsWith(COMANDO_VALIDO)
                }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.Proposta())
                verifies()
            }
        }
    }

    @Test
    fun `deve possuir 2 assinadores obigatórios - proponente e oblito`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { output(it, propostaState) }
                tweak {
                    command(listOf(alice.publicKey, charlie.publicKey), NegociacaoContract.Commands.Proposta())
                    failsWith(ASSINATURA_OBLITO_OBRIGATORIA)
                }
                tweak {
                    command(listOf(charlie.publicKey, bob.publicKey), NegociacaoContract.Commands.Proposta())
                    failsWith(ASSINATURA_PROPONENTE_OBRIGATORIA)
                }
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.Proposta())
                verifies()
            }
        }
    }

    @Test
    fun `deve validar transações em que comprador e vendedor são proponente e oblito respectivamente`() {
        ledgerServices.ledger {
            transaction {
                command(listOf(alice.publicKey, bob.publicKey), NegociacaoContract.Commands.Proposta())
                tweak {
                    // Order reversed - buyer = proposee, seller = proposer
                    NegociacaoContract.ID?.let { output(it, propostaStateInvalida1) }
                    verifies()
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaStateInvalida2) }
                    failsWith(COMPRADOR_VENDEDOR)
                }
                tweak {
                    NegociacaoContract.ID?.let { output(it, propostaStateInvalida3) }
                    failsWith(COMPRADOR_VENDEDOR)
                }
                NegociacaoContract.ID?.let { output(it, propostaState) }
                verifies()
            }
        }
    }

    @Test
    fun `deve validar propostas sem input e sem timestamp`() {
        ledgerServices.ledger {
            transaction {
                NegociacaoContract.ID?.let { output(it, propostaState) }
                command(
                    listOf(alice.publicKey, bob.publicKey),
                    NegociacaoContract.Commands.Proposta()
                )
                tweak {
                    input(DummyContract.PROGRAM_ID, DummyState())
                    failsWith(NENHUM_INPUT)
                }
                tweak {
                    timeWindow(Instant.now())
                    failsWith(NENHUM_TIMESTAMP)
                }
            }
        }
    }
}
