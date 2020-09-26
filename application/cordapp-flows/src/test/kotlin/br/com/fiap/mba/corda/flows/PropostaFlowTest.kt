package br.com.fiap.mba.corda.flows

import br.com.fiap.mba.corda.states.PropostaState
import groovy.util.GroovyTestCase.assertEquals
import net.corda.core.node.services.queryBy
import net.corda.testing.internal.chooseIdentity
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.ExecutionException
import kotlin.test.assertFailsWith

class PropostaFlowTest : FlowTestsBase() {

    companion object {

        private val VALOR_COTACAO_REAL_VALIDO = BigDecimal.ONE
        private val VALOR_TAXA_VALIDO = BigDecimal.TEN

        private const val MOEDA_VALIDA = "USD"
        private const val VALOR_QUANTIDADE_VALIDO = 2
        private const val VALOR_NEGATIVO = -1
        private const val VALOR_ZERO = 0
    }

    @Test
    fun `deve ser válido`() {

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
    fun `não deve aceitar transações consigo mesmo`() {

        val nodeA = this.a.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeA,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_QUANTIDADE_VALIDO,
                cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
                taxa = VALOR_TAXA_VALIDO
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("O comprador e o vendedor devem ser diferentes!", exception.message)
    }

    @Test
    fun `não deve aceitar transações com a quantidade zero`() {

        val nodeB = this.b.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeB,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_ZERO,
                cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
                taxa = VALOR_TAXA_VALIDO
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("A quantidade deve ser maior que zero!", exception.message)
    }

    @Test
    fun `não deve aceitar transações com quantidade negativa`() {

        val nodeB = this.b.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeB,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_NEGATIVO,
                cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
                taxa = VALOR_TAXA_VALIDO
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("A quantidade deve ser maior que zero!", exception.message)
    }

    @Test
    fun `não deve aceitar transações com cotacaoReal zero`() {

        val nodeB = this.b.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeB,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_QUANTIDADE_VALIDO,
                cotacaoReal = BigDecimal.ZERO,
                taxa = VALOR_TAXA_VALIDO
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("A cotação do Real deve ser maior que zero!", exception.message)
    }

    @Test
    fun `não deve aceitar transações com cotacaoReal negativa`() {

        val nodeB = this.b.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeB,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_QUANTIDADE_VALIDO,
                cotacaoReal = BigDecimal(VALOR_NEGATIVO),
                taxa = VALOR_TAXA_VALIDO
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("A cotação do Real deve ser maior que zero!", exception.message)
    }

    @Test
    fun `não deve aceitar transações com taxa zero`() {

        val nodeB = this.b.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeB,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_QUANTIDADE_VALIDO,
                cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
                taxa = BigDecimal.ZERO
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("A taxa deve ser maior que zero!", exception.message)
    }

    @Test
    fun `não deve aceitar transações com taxa negativa`() {

        val nodeB = this.b.info.chooseIdentity()

        val exception = assertFailsWith<ExecutionException> {

            this.nodeACriarProposta(
                instituicaoFinanceira = nodeB,
                moeda = MOEDA_VALIDA,
                quantidade = VALOR_QUANTIDADE_VALIDO,
                cotacaoReal = VALOR_COTACAO_REAL_VALIDO,
                taxa = BigDecimal(VALOR_NEGATIVO)
            )
        }.cause!!

        assertEquals(IllegalArgumentException::class, exception::class)
        assertEquals("A taxa deve ser maior que zero!", exception.message)
    }
}
