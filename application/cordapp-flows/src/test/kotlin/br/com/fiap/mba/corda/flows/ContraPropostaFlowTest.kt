package br.com.fiap.mba.corda.flows

import br.com.fiap.mba.corda.states.PropostaState
import net.corda.core.flows.FlowException
import net.corda.core.node.services.queryBy
import net.corda.testing.internal.chooseIdentity
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContraPropostaFlowTest : FlowTestsBase() {

    companion object {

        private val PRIMEIRA_CONTRA_PROPOSTA = BigDecimal(15)
        private val SEGUNDA_CONTRA_PROPOSTA = BigDecimal(15)
        private val VALOR_COTACAO_REAL_VALIDO = BigDecimal.ONE
        private val VALOR_TAXA_VALIDO = BigDecimal.TEN

        private const val MOEDA_VALIDA = "USD"
        private const val VALOR_QUANTIDADE_VALIDO = 2
    }

    @Test
    fun `deve ser válido 1 contra-proposta`() {

        val instituicaoFinanceira = b.info.chooseIdentity()

        val propostaId = this.nodeACriarProposta(
            instituicaoFinanceira = instituicaoFinanceira,
            moeda = MOEDA_VALIDA,
            quantidade = VALOR_QUANTIDADE_VALIDO,
            cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
            taxa = VALOR_TAXA_VALIDO
        )

        this.nodeBContraProposta(
            propostaId = propostaId,
            novaTaxa = PRIMEIRA_CONTRA_PROPOSTA
        )

        for (node in listOf(a, b)) {
            node.transaction {

                val propostaStates = node.services.vaultService.queryBy<PropostaState>().states
                assertEquals(1, propostaStates.size)

                val proposta = propostaStates.single().state.data

                assertEquals(PRIMEIRA_CONTRA_PROPOSTA, proposta.taxa)

                assertEquals(a.info.chooseIdentity(), proposta.comprador)
                assertEquals(a.info.chooseIdentity(), proposta.oblato)

                assertEquals(b.info.chooseIdentity(), proposta.vendedor)
                assertEquals(b.info.chooseIdentity(), proposta.proponente)
            }
        }
    }

    @Test
    fun `deve ser válido 2 contra-propostas`() {

        val instituicaoFinanceira = b.info.chooseIdentity()

        val propostaId = this.nodeACriarProposta(
            instituicaoFinanceira = instituicaoFinanceira,
            moeda = MOEDA_VALIDA,
            quantidade = VALOR_QUANTIDADE_VALIDO,
            cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
            taxa = VALOR_TAXA_VALIDO
        )

        this.nodeBContraProposta(
            propostaId = propostaId,
            novaTaxa = PRIMEIRA_CONTRA_PROPOSTA
        )

        this.nodeAContraProposta(
            propostaId = propostaId,
            novaTaxa = SEGUNDA_CONTRA_PROPOSTA
        )

        for (node in listOf(a, b)) {
            node.transaction {

                val propostaStates = node.services.vaultService.queryBy<PropostaState>().states
                assertEquals(1, propostaStates.size)

                val proposta = propostaStates.single().state.data

                assertEquals(SEGUNDA_CONTRA_PROPOSTA, proposta.taxa)

                assertEquals(a.info.chooseIdentity(), proposta.comprador)
                assertEquals(a.info.chooseIdentity(), proposta.proponente)

                assertEquals(b.info.chooseIdentity(), proposta.vendedor)
                assertEquals(b.info.chooseIdentity(), proposta.oblato)
            }
        }
    }

    @Test
    fun `não deve aceitar contra-propostas caso o seja do mesmo proponente`() {

        val instituicaoFinanceira = b.info.chooseIdentity()

        val propostaId = this.nodeACriarProposta(
            instituicaoFinanceira = instituicaoFinanceira,
            moeda = MOEDA_VALIDA,
            quantidade = VALOR_QUANTIDADE_VALIDO,
            cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
            taxa = VALOR_TAXA_VALIDO
        )

        val flow = ContraPropostaFlow.Initiator(
            propostaId = propostaId,
            novaTaxa = PRIMEIRA_CONTRA_PROPOSTA
        )
        val future = a.startFlow(flow)

        network.runNetwork()

        val exceptionFromFlow = assertFailsWith<ExecutionException> {
            future.get()
        }.cause!!

        assertEquals(FlowException::class, exceptionFromFlow::class)
        assertEquals("Só um oblato pode realizar uma contra-proposta.", exceptionFromFlow.message)
    }
}
