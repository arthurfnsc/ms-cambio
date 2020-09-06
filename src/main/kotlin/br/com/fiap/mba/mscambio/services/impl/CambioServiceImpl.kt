package br.com.fiap.mba.mscambio.services.impl

import br.com.fiap.mba.mscambio.corda.flows.PropostaFlow
import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import br.com.fiap.mba.mscambio.exceptions.PropostaInvalidaException
import br.com.fiap.mba.mscambio.services.CambioService
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.utilities.getOrThrow
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
        const val I18N_REQUISICAO_INVALIDA = "requisicao.invalida"
    }

    override fun alterarStatusTransicao() {
        TODO("Not yet implemented")
    }

    override fun enviarPropostaNegociacao(
        propostaNegociacao: EnvioPropostaDTO,
        proxy: CordaRPCOps
    ): UniqueIdentifier {
        val remetente = proxy.nodeInfo().legalIdentities[0]

        require(remetente.toString() != BANCO_ID) {

            val mensagem = this.messageSource.getMessage(I18N_REMETENTE_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }

        require(remetente.toString() != propostaNegociacao.instituicaoFinanceira) {

            val mensagem = this.messageSource.getMessage(I18N_DESTINATARIO_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }

        return try {

            proxy.startTrackedFlow (
                PropostaFlow::Initiator,
                    remetente,
                    propostaNegociacao.moeda,
                    propostaNegociacao.quantidade,
                    propostaNegociacao.cotacaoReal,
                    propostaNegociacao.taxa
            ).returnValue.getOrThrow()
        } catch (ex: Throwable) {

            throw PropostaInvalidaException(ex.stackTraceToString())
        }
    }

    override fun recuperarPropostaNegociacao() {
        TODO("Not yet implemented")
    }

    override fun recuperarTransicoesDisponiveis() {
        TODO("Not yet implemented")
    }
}
