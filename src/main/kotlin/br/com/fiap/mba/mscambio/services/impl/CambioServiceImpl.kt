package br.com.fiap.mba.mscambio.services.impl

import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import br.com.fiap.mba.mscambio.exceptions.PropostaNegociacaoInvalidaException
import br.com.fiap.mba.mscambio.services.CambioService
import net.corda.core.messaging.CordaRPCOps
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
open class CambioServiceImpl(
    private val messageSource: MessageSource
) : CambioService{

    companion object {

        const val BANCO_ID = "O=Banco, L=Brasilia, C=BR"
        const val I18N_DESTINATARIO_INVALIDO = "destinatario.invalido"
        const val I18N_REMETENTE_INVALIDO = "remetente.invalido"
    }

    override fun alterarStatusTransicao() {
        TODO("Not yet implemented")
    }

    override fun enviarPropostaNegociacao(
        propostaNegociacao: EnvioPropostaDTO,
        proxy: CordaRPCOps
    ) {
        val remetente = proxy.nodeInfo().legalIdentities[0].toString()

        require(remetente != BANCO_ID) {

            val mensagem = this.messageSource.getMessage(I18N_REMETENTE_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaNegociacaoInvalidaException(mensagem)
        }

        require(remetente != propostaNegociacao.instituicaoFinanceira) {

            val mensagem = this.messageSource.getMessage(I18N_DESTINATARIO_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaNegociacaoInvalidaException(mensagem)
        }
    }

    override fun recuperarPropostaNegociacao() {
        TODO("Not yet implemented")
    }

    override fun recuperarTransicoesDisponiveis() {
        TODO("Not yet implemented")
    }
}
