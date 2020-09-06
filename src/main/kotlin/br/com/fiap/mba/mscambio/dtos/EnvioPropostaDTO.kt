package br.com.fiap.mba.mscambio.dtos

import com.github.pozo.KotlinBuilder
import net.corda.core.messaging.CordaRPCOps
import java.math.BigDecimal

@KotlinBuilder
data class EnvioPropostaDTO(
    val instituicaoFinanceira: String,
    val moeda: String,
    val valor: BigDecimal,
    val taxa: BigDecimal
)
