package br.com.fiap.mba.mscambio.factories

import org.openapi.cambio.server.model.Erro
import org.openapi.cambio.server.model.ParametroInvalido
import org.springframework.http.HttpStatus

class ErroFactory {

    private constructor()

    companion object {

        fun criar(
            httpStatus: HttpStatus
        ): Erro {

            val erro = Erro()
            erro.httpCode = httpStatus.value()
            erro.httpMessage = httpStatus.reasonPhrase

            return erro
        }

        fun criar(
            httpStatus: HttpStatus,
            descricao: String
        ): Erro {

            val erro = criar(httpStatus)
            erro.descricao = descricao

            return erro
        }

        fun criar(
            httpStatus: HttpStatus,
            descricao: String,
            parametrosInvalidos: List<ParametroInvalido>
        ): Erro {

            val erro = criar(httpStatus, descricao)
            erro.parametrosInvalidos = parametrosInvalidos

            return erro
        }
    }
}
