package br.com.fiap.mba.mscambio.converters

import br.com.fiap.mba.mscambio.dtos.EnvioPropostaDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.openapi.cambio.server.model.PropostaNegociacaoRequest

@Mapper(
    componentModel = "spring"
)
interface EnvioPropostaConverter {

    @Mappings(
        Mapping(
            source = "nomeMoeda",
            target = "moeda"
        ),
        Mapping(
            source = "valorMoeda",
            target = "valor"
        ),
        Mapping(
            source = "taxaPretendida",
            target = "taxa"
        )
    )
    fun convert(element: PropostaNegociacaoRequest) : EnvioPropostaDTO
}