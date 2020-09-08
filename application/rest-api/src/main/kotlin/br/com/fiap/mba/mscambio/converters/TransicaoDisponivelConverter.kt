package br.com.fiap.mba.mscambio.converters

import br.com.fiap.mba.mscambio.dtos.Transicao
import org.mapstruct.Mapper
import org.openapi.cambio.server.model.TransicaoDisponivel

@Mapper(
    componentModel = "spring"
)
interface TransicaoDisponivelConverter {

    fun from(element: TransicaoDisponivel.TransicaoEnum): Transicao
}