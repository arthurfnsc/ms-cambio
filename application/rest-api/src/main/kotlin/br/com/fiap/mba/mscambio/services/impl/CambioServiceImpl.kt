package br.com.fiap.mba.mscambio.services.impl

import br.com.fiap.mba.corda.flows.PropostaFlow
import br.com.fiap.mba.corda.states.PropostaState
import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import br.com.fiap.mba.mscambio.exceptions.PropostaInvalidaException
import br.com.fiap.mba.mscambio.gateways.NodeRPCConnection
import br.com.fiap.mba.mscambio.resources.impl.CambioResourceImpl
import br.com.fiap.mba.mscambio.services.CambioService
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.utilities.getOrThrow
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
open class CambioServiceImpl(
    private val rpc: NodeRPCConnection,
    private val messageSource: MessageSource
) : CambioService {

    companion object {

        const val BANCO_ID = "O=Banco, L=Brasilia, C=BR"
        const val I18N_DESTINATARIO_INVALIDO = "destinatario.invalido"
        const val I18N_ID_INVALIDO = "id.invalido"
        const val I18N_REMETENTE_INVALIDO = "remetente.invalido"
        const val I18N_REQUISICAO_INVALIDA = "requisicao.invalida"
    }

    private val proxy = rpc.proxy

    override fun alterarStatusTransicao() {
        TODO("Not yet implemented")
    }

    override fun enviarPropostaNegociacao(
        propostaNegociacao: EnvioPropostaDTO
    ): UniqueIdentifier {

        val x500Name = propostaNegociacao.instituicaoFinanceira?.let { CordaX500Name.parse(it) }

        requireNotNull(x500Name) {

            val mensagem = this.messageSource.getMessage(CambioResourceImpl.I18N_INSTITUICAO_INVALIDA, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }

        val remetente = this.proxy.nodeInfo().legalIdentities[0]

        require(remetente.toString() != BANCO_ID) {

            val mensagem = this.messageSource.getMessage(I18N_REMETENTE_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }

        require(remetente.toString() != propostaNegociacao.instituicaoFinanceira) {

            val mensagem = this.messageSource.getMessage(I18N_DESTINATARIO_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }

        return try {

            this.proxy.startTrackedFlow (
                PropostaFlow::Initiator,
                    remetente,
                    propostaNegociacao.moeda,
                    propostaNegociacao.quantidade,
                    propostaNegociacao.cotacaoReal,
                    propostaNegociacao.taxa
            ).returnValue.getOrThrow()
        } catch (ex: Throwable) {

            val mensagem = this.messageSource.getMessage(I18N_REQUISICAO_INVALIDA, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }
    }

    override fun recuperarPropostaNegociacao(
        uuid: UUID?
    ): StateAndRef<PropostaState> {

        requireNotNull(uuid) {

            val mensagem = this.messageSource.getMessage(I18N_ID_INVALIDO, null, Locale("pt", "BR"))

            throw PropostaInvalidaException(mensagem)
        }

        val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)

        return this.proxy.vaultQueryBy<PropostaState>(criteria).states.first()
    }

    override fun recuperarTransicoesDisponiveis() {
        TODO("Not yet implemented")
    }
}
