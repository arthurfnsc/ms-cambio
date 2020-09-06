package br.com.fiap.mba.mscambio.resources.impl

import br.com.fiap.mba.mscambio.converters.EnvioPropostaConverter
import br.com.fiap.mba.mscambio.exceptions.PropostaNegociacaoInvalidaException
import br.com.fiap.mba.mscambio.rpc.NodeRPCConnection
import br.com.fiap.mba.mscambio.services.CambioService
import net.corda.core.identity.CordaX500Name
import org.openapi.cambio.server.api.V1Api
import org.openapi.cambio.server.model.PropostaNegociacao
import org.openapi.cambio.server.model.PropostaNegociacaoRequest
import org.openapi.cambio.server.model.TransicaoDisponivel
import org.springframework.context.MessageSource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
open class CambioResourceImpl(
    private val envioPropostaConverter: EnvioPropostaConverter,
    private val messageSource: MessageSource,
    private val service: CambioService,
    rpc: NodeRPCConnection
) : V1Api {

    companion object {

        const val I18N_INSTITUICAO_INVALIDA = "instituicao.invalida"
    }

    private val proxy = rpc.proxy

    override fun alterarStatusTransicao(
        id: UUID?
    ): ResponseEntity<PropostaNegociacao> {
        return super.alterarStatusTransicao(id)
    }

    override fun enviarPropostaNegociacao(
        propostaNegociacaoRequest: PropostaNegociacaoRequest?
    ): ResponseEntity<PropostaNegociacao> {

        val x500Name = propostaNegociacaoRequest?.instituicaoFinanceira?.let { CordaX500Name.parse(it) }

        requireNotNull(x500Name) {

            val mensagem = this.messageSource.getMessage(I18N_INSTITUICAO_INVALIDA, null, Locale("pt", "BR"))

            throw PropostaNegociacaoInvalidaException(mensagem)
        }

        val dto = this.envioPropostaConverter.convert(propostaNegociacaoRequest)

        this.service.enviarPropostaNegociacao(
            dto,
            proxy
        )

        return super.enviarPropostaNegociacao(propostaNegociacaoRequest)
    }

    override fun recuperarPropostaNegociacao(
        id: UUID?
    ): ResponseEntity<PropostaNegociacao> {
        return super.recuperarPropostaNegociacao(id)
    }

    override fun recuperarTransicoesDisponiveis(
        id: UUID?
    ): ResponseEntity<MutableList<TransicaoDisponivel>> {
        return super.recuperarTransicoesDisponiveis(id)
    }
}
