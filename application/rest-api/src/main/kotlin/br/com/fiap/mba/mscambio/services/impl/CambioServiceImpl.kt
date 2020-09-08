package br.com.fiap.mba.mscambio.services.impl

import br.com.fiap.mba.corda.flows.AceitePropostaFlow
import br.com.fiap.mba.corda.flows.ContraPropostaFlow
import br.com.fiap.mba.corda.flows.PropostaFlow
import br.com.fiap.mba.corda.flows.RecusaPropostaFlow
import br.com.fiap.mba.corda.states.PropostaState
import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import br.com.fiap.mba.mscambio.dtos.Transicao
import br.com.fiap.mba.mscambio.exceptions.DestinatarioException
import br.com.fiap.mba.mscambio.exceptions.InstituicaoFinanceiraException
import br.com.fiap.mba.mscambio.exceptions.RemetenteException
import br.com.fiap.mba.mscambio.gateways.NodeRPCConnection
import br.com.fiap.mba.mscambio.resources.impl.CambioResourceImpl
import br.com.fiap.mba.mscambio.services.CambioService
import net.corda.core.CordaException
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
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
    rpc: NodeRPCConnection,
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

    override fun alterarStatusTransicao(
        uuid: UUID?,
        transicao: Transicao,
        novaTaxa: Double?
    ) {

        try {

            when(transicao) {

                Transicao.ACEITADO -> this.proxy.startTrackedFlow(
                    AceitePropostaFlow::Initiator,
                    UniqueIdentifier(id = uuid!!)
                ).returnValue.getOrThrow()

                Transicao.CONTRA_PROPOSTA -> this.proxy.startTrackedFlow(
                    ContraPropostaFlow::Initiator,
                    UniqueIdentifier(id = uuid!!),
                    novaTaxa?.toBigDecimal()
                ).returnValue.getOrThrow()

                Transicao.REJEITADO -> this.proxy.startTrackedFlow(
                    RecusaPropostaFlow::Initiator,
                    UniqueIdentifier(id = uuid!!)
                ).returnValue.getOrThrow()
            }

        } catch (ex: Throwable) {

            val mensagem = this.messageSource.getMessage(I18N_REQUISICAO_INVALIDA, null, Locale("pt", "BR"))

            throw CordaException(mensagem)
        }
    }

    override fun enviarPropostaNegociacao(
        propostaNegociacao: EnvioPropostaDTO
    ): UniqueIdentifier {

        val x500Name = propostaNegociacao.instituicaoFinanceira?.let {
            CordaX500Name.parse(it)
        }

        requireNotNull(x500Name) {

            val mensagem = this.messageSource.getMessage(CambioResourceImpl.I18N_INSTITUICAO_INVALIDA, null, Locale("pt", "BR"))

            throw InstituicaoFinanceiraException(mensagem)
        }

        val remetente = this.proxy.nodeInfo().legalIdentities[0]

        require(remetente.toString() != BANCO_ID) {

            val mensagem = this.messageSource.getMessage(I18N_REMETENTE_INVALIDO, null, Locale("pt", "BR"))

            throw RemetenteException(mensagem)
        }

        require(remetente.toString() != propostaNegociacao.instituicaoFinanceira) {

            val mensagem = this.messageSource.getMessage(I18N_DESTINATARIO_INVALIDO, null, Locale("pt", "BR"))

            throw DestinatarioException(mensagem)
        }

        return try {

            this.proxy.startTrackedFlow (
                PropostaFlow::Initiator,
                    this.proxy.wellKnownPartyFromX500Name(x500Name)!!,
                    propostaNegociacao.moeda,
                    propostaNegociacao.quantidade,
                    propostaNegociacao.cotacaoReal,
                    propostaNegociacao.taxa
            ).returnValue.getOrThrow()
        } catch (ex: Throwable) {

            val mensagem = this.messageSource.getMessage(I18N_REQUISICAO_INVALIDA, null, Locale("pt", "BR"))

            throw CordaException(mensagem)
        }
    }

    override fun recuperarPropostaNegociacao(
        uuid: UUID?
    ): StateAndRef<PropostaState> {

        requireNotNull(uuid) {

            val mensagem = this.messageSource.getMessage(I18N_ID_INVALIDO, null, Locale("pt", "BR"))

            throw RemetenteException(mensagem)
        }

        val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)

        val statesProposta = this.proxy.vaultQueryBy<PropostaState>(criteria).states

        val filteredStates = statesProposta.filter {
            it.state.data.linearId == UniqueIdentifier(id = uuid)
        }

        return filteredStates.last()
    }

    override fun recuperarTransicoesDisponiveis() {
        TODO("Not yet implemented")
    }
}
