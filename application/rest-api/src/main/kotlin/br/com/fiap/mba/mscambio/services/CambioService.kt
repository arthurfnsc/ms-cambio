package br.com.fiap.mba.mscambio.services

import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.CordaRPCOps

interface CambioService {

    fun alterarStatusTransicao()

    fun enviarPropostaNegociacao(
        propostaNegociacao: EnvioPropostaDTO,
        proxy: CordaRPCOps
    ): UniqueIdentifier

    fun recuperarPropostaNegociacao()

    fun recuperarTransicoesDisponiveis()
}
