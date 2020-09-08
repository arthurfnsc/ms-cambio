package br.com.fiap.mba.mscambio.converters

import br.com.fiap.mba.corda.states.PropostaState
import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.openapi.cambio.server.model.PropostaNegociacao
import org.openapi.cambio.server.model.PropostaNegociacaoRequest

@Mapper(
    componentModel = "spring"
)
interface PropostaConverter {

    @Mappings(
        Mapping(
            source = "nomeMoeda",
            target = "moeda"
        ),
        Mapping(
            source = "quantidadeMoeda",
            target = "quantidade"
        ),
        Mapping(
            source = "taxaPretendida",
            target = "taxa"
        )
    )
    fun from(element: PropostaNegociacaoRequest): EnvioPropostaDTO

    @Mappings(
        Mapping(
            expression = "java(element.getLinearId().getId())",
            target = "id"
        ),
        Mapping(
            expression = "java(element.getVendedor().toString())",
            target = "instituicaoFinanceira"
        ),
        Mapping(
            source = "moeda",
            target = "nomeMoeda"
        ),
        Mapping(
            source = "quantidade",
            target = "quantidadeMoeda"
        ),
        Mapping(
            source = "taxa",
            target = "taxaPretendida"
        )
    )
    fun from(element: PropostaState): PropostaNegociacao
}
