package br.com.fiap.mba.mscambio.corda.states

import br.com.fiap.mba.mscambio.corda.contracts.NegociacaoContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.math.BigDecimal

@BelongsToContract(NegociacaoContract::class)
data class NegociacaoState(
    val taxa: BigDecimal,
    val comprador: Party,
    val vendedor: Party,
    override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState {
    override val participants = listOf(comprador, vendedor)
}