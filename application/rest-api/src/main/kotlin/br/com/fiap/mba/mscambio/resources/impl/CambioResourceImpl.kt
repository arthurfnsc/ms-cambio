package br.com.fiap.mba.mscambio.resources.impl

import br.com.fiap.mba.mscambio.converters.PropostaConverter
import br.com.fiap.mba.mscambio.services.CambioService
import org.openapi.cambio.server.api.V1Api
import org.openapi.cambio.server.model.PropostaNegociacao
import org.openapi.cambio.server.model.PropostaNegociacaoRequest
import org.openapi.cambio.server.model.PropostaNegociacaoResponse
import org.openapi.cambio.server.model.TransicaoDisponivel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
open class CambioResourceImpl(
    private val propostaConverter: PropostaConverter,
    private val service: CambioService
) : V1Api {

    companion object {

        const val I18N_INSTITUICAO_INVALIDA = "instituicao.invalida"
    }

    override fun alterarStatusTransicao(
        id: UUID?
    ): ResponseEntity<PropostaNegociacao> {
        return super.alterarStatusTransicao(id)
    }

    override fun enviarPropostaNegociacao(
        propostaNegociacaoRequest: PropostaNegociacaoRequest
    ): ResponseEntity<PropostaNegociacaoResponse> {

        val dto = this.propostaConverter.from(propostaNegociacaoRequest)

        val uniqueIdentifier = this.service.enviarPropostaNegociacao(dto)

        val response = PropostaNegociacaoResponse()
        response.id = uniqueIdentifier.id

        return ResponseEntity(response, HttpStatus.CREATED)
    }

    override fun recuperarPropostaNegociacao(
        id: UUID?
    ): ResponseEntity<PropostaNegociacao> {

        val propostaState = this.service.recuperarPropostaNegociacao(id).state.data

        System.out.println(propostaState)

        val response = this.propostaConverter.from(propostaState)

        return ResponseEntity(response, HttpStatus.OK)
    }

    override fun recuperarTransicoesDisponiveis(
        id: UUID?
    ): ResponseEntity<MutableList<TransicaoDisponivel>> {
        return super.recuperarTransicoesDisponiveis(id)
    }
}
