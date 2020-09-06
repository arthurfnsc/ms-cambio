package br.com.fiap.mba.corda.states

import br.com.fiap.mba.corda.contracts.NegociacaoContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.math.BigDecimal

@BelongsToContract(NegociacaoContract::class)
data class PropostaState(
    val comprador: Party,
    val proponente: Party,
    val vendedor: Party,
    val oblato: Party,
    val moeda: String,
    val quantidade: Int,
    val cotacaoReal: BigDecimal,
    val taxa: BigDecimal,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState {
    override val participants = listOf(proponente, oblato)
}
