package br.com.fiap.mba.corda.states

import br.com.fiap.mba.corda.contracts.NegociacaoContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.math.BigDecimal
import java.time.LocalDateTime

@BelongsToContract(NegociacaoContract::class)
data class PropostaAceitaState(
    val statusNegociacao: String,
    val comprador: Party,
    val vendedor: Party,
    val moeda: String,
    val quantidade: Int,
    val cotacaoReal: BigDecimal,
    val taxa: BigDecimal,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = criadoEm,
    val aceitadoEm: LocalDateTime? = null,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState {
    override val participants = listOf(comprador, vendedor)
}