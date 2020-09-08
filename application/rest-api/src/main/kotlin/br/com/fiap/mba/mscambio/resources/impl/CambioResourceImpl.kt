package br.com.fiap.mba.mscambio.resources.impl

import br.com.fiap.mba.mscambio.converters.PropostaConverter
import br.com.fiap.mba.mscambio.converters.TransicaoDisponivelConverter
import br.com.fiap.mba.mscambio.services.CambioService
import org.openapi.cambio.server.api.V1Api
import org.openapi.cambio.server.model.AlterarTransicaoRequest
import org.openapi.cambio.server.model.PropostaNegociacao
import org.openapi.cambio.server.model.PropostaNegociacaoRequest
import org.openapi.cambio.server.model.PropostaNegociacaoResponse
import org.openapi.cambio.server.model.TransicaoDisponivel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
open class CambioResourceImpl(
    private val propostaConverter: PropostaConverter,
    private val transicaoDisponivelConverter: TransicaoDisponivelConverter,
    private val service: CambioService
) : V1Api {

    override fun alterarStatusTransicao(
        id: UUID?,
        alterarTransicaoRequest: AlterarTransicaoRequest?
    ): ResponseEntity<Void>? {

        val enum = alterarTransicaoRequest!!.novaTransicao.transicao

        val transicao = transicaoDisponivelConverter.from(enum)

        this.service.alterarStatusTransicao(id, transicao, alterarTransicaoRequest.novaTaxa)

        return ResponseEntity(HttpStatus.NO_CONTENT)
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

        val response = this.propostaConverter.from(propostaState)

        return ResponseEntity(response, HttpStatus.OK)
    }

    override fun recuperarTransicoesDisponiveis(
        id: UUID?
    ): ResponseEntity<MutableList<TransicaoDisponivel>> {

        val transicaoAtual = this.service.recuperarTransicoesDisponiveis(id)

        val transicoesDisponiveis = arrayListOf<TransicaoDisponivel>()

        if (
            transicaoAtual == "ABERTO" ||
            transicaoAtual == "CONTRA_PROPOSTA"
        ) {

            val transicaoAceitado = TransicaoDisponivel()
            transicaoAceitado.transicao = TransicaoDisponivel.TransicaoEnum.ACEITADO

            val transicaoContraProposta = TransicaoDisponivel()
            transicaoContraProposta.transicao = TransicaoDisponivel.TransicaoEnum.CONTRA_PROPOSTA

            val transicaoRejeitado = TransicaoDisponivel()
            transicaoRejeitado.transicao = TransicaoDisponivel.TransicaoEnum.REJEITADO

            transicoesDisponiveis.add(transicaoAceitado)
            transicoesDisponiveis.add(transicaoContraProposta)
            transicoesDisponiveis.add(transicaoRejeitado)
        }

        return ResponseEntity(transicoesDisponiveis.toMutableList(), HttpStatus.OK)
    }
}
