package br.com.fiap.mba.corda.flows

import br.com.fiap.mba.corda.states.PropostaState
import groovy.util.GroovyTestCase.assertEquals
import net.corda.core.internal.rootMessage
import net.corda.core.node.services.queryBy
import net.corda.testing.internal.chooseIdentity
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.concurrent.ExecutionException

class PropostaFlowShould: FlowTestsBase() {

    companion object {

        private val VALOR_COTACAO_REAL_VALIDO = BigDecimal.ONE
        private val VALOR_TAXA_VALIDO = BigDecimal.TEN

        private const val VALOR_QUANTIDADE_VALIDO = 2
        private const val MOEDA_VALIDA = "USD"
    }

    @Test
    fun `be valid`() {

        val nodeA = this.a.info.chooseIdentity()
        val nodeB = this.b.info.chooseIdentity()

        this.nodeACriarProposta(
            instituicaoFinanceira = nodeB,
            moeda = MOEDA_VALIDA,
            quantidade = VALOR_QUANTIDADE_VALIDO,
            cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
            taxa = VALOR_TAXA_VALIDO
        )

        for (node in listOf(a, b)) {
            node.transaction {

                val propostas = node.services.vaultService.queryBy<PropostaState>().states
                assertEquals(1, propostas.size)

                val proposta = propostas.single().state.data

                assertEquals(nodeA, proposta.proponente)
                assertEquals(nodeA, proposta.comprador)

                assertEquals(nodeB, proposta.oblato)
                assertEquals(nodeB, proposta.vendedor)

                assertEquals("ABERTO", proposta.statusTransacao)

                assertEquals(MOEDA_VALIDA, proposta.moeda)
                assertEquals(VALOR_QUANTIDADE_VALIDO, proposta.quantidade)
                assertEquals(VALOR_COTACAO_REAL_VALIDO, proposta.cotacaoReal)
                assertEquals(VALOR_TAXA_VALIDO, proposta.taxa)
            }
        }
    }

    @Test
    fun `not accept transactions with itself`() {

        val nodeA = this.a.info.chooseIdentity()

        val exception = assertThrows(ExecutionException::class.java) {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeA,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_QUANTIDADE_VALIDO,
                cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
                taxa = VALOR_TAXA_VALIDO
            )
        }

        assertEquals("O comprador e o vendedor devem ser diferentes!", exception.cause!!.message)
    }
}
