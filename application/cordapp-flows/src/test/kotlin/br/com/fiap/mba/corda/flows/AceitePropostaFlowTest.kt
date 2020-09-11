package br.com.fiap.mba.corda.flows

import br.com.fiap.mba.corda.states.PropostaState
import net.corda.core.flows.FlowException
import net.corda.core.node.services.queryBy
import net.corda.testing.internal.chooseIdentity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AceitePropostaFlowTest : FlowTestsBase() {

    companion object {

        private val VALOR_COTACAO_REAL_VALIDO = BigDecimal.ONE
        private val VALOR_TAXA_VALIDO = BigDecimal.TEN

        private const val MOEDA_VALIDA = "USD"
        private const val VALOR_QUANTIDADE_VALIDO = 2
    }

    @Test
    fun `deve ser válido`() {

        val instituicaoFinanceira = b.info.chooseIdentity()

        val propostaId = this.nodeACriarProposta(
            instituicaoFinanceira = instituicaoFinanceira,
            moeda = MOEDA_VALIDA,
            quantidade = VALOR_QUANTIDADE_VALIDO,
            cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
            taxa = VALOR_TAXA_VALIDO
        )

        this.nodeBAceitaProposta(propostaId)

        for (node in listOf(a, b)) {
            node.transaction {

                val propostaStates = node.services.vaultService.queryBy<PropostaState>().states
                assertEquals(1, propostaStates.size)

                val proposta = propostaStates.single().state.data

                assertEquals(VALOR_TAXA_VALIDO, proposta.taxa)

                assertThat(a.info.chooseIdentity()).isEqualTo(proposta.comprador)
                assertThat(b.info.chooseIdentity()).isEqualTo(proposta.vendedor)
            }
        }
    }

    @Test
    fun `não deve aceitar propostas caso o seja do mesmo proponente`() {

        val instituicaoFinanceira = b.info.chooseIdentity()

        val propostaId = this.nodeACriarProposta(
            instituicaoFinanceira = instituicaoFinanceira,
            moeda = MOEDA_VALIDA,
            quantidade = VALOR_QUANTIDADE_VALIDO,
            cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
            taxa = VALOR_TAXA_VALIDO
        )

        val flow = AceitePropostaFlow.Initiator(propostaId)
        val future = a.startFlow(flow)

        this.network.runNetwork()

        val exceptionFromFlow = assertFailsWith<ExecutionException> {
            future.get()
        }.cause!!

        assertEquals(FlowException::class, exceptionFromFlow::class)
        assertEquals("Só um oblato pode aceitar a proposta.", exceptionFromFlow.message)
    }
}
