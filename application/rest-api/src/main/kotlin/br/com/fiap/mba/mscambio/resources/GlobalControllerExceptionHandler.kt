package br.com.fiap.mba.mscambio.resources

import br.com.fiap.mba.mscambio.exceptions.DestinatarioException
import br.com.fiap.mba.mscambio.exceptions.InstituicaoFinanceiraException
import br.com.fiap.mba.mscambio.exceptions.PropostaInvalidaException
import br.com.fiap.mba.mscambio.exceptions.RemetenteException
import br.com.fiap.mba.mscambio.factories.ErroFactory
import net.corda.core.CordaException
import org.openapi.cambio.server.model.Erro
import org.openapi.cambio.server.model.ParametroInvalido
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalControllerExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {

        private const val DESTINATARIO_INVALIDO = "Destinatário inválido!"
        private const val INSTITUICAO_FINANCEIRA_INVALIDA = "Instituição Financeira inválida!"
        private const val REMETENTE_INVALIDO = "Remetente inválido!"
        private const val ID_PROPOSTA_INVALIDO = "Identificador de proposta inválido!"
    }

    @ExceptionHandler(CordaException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun cordaException(ex: CordaException): Erro {

        return ErroFactory.criar(
            HttpStatus.BAD_REQUEST,
            ex.message!!
        )
    }

    @ExceptionHandler(DestinatarioException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun destinatarioException(ex: DestinatarioException): Erro {

        val parametroInvalido = ParametroInvalido()
        parametroInvalido.nome = "instituicao_financeira"
        parametroInvalido.descricao = ex.message

        return ErroFactory.criar(
            HttpStatus.BAD_REQUEST,
            DESTINATARIO_INVALIDO,
            listOf(
                parametroInvalido
            )
        )
    }

    @ExceptionHandler(InstituicaoFinanceiraException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun instituicaoFinanceiraInvalida(ex: InstituicaoFinanceiraException): Erro {

        val parametroInvalido = ParametroInvalido()
        parametroInvalido.nome = "instituicao_financeira"
        parametroInvalido.descricao = ex.message

        return ErroFactory.criar(
            HttpStatus.BAD_REQUEST,
            INSTITUICAO_FINANCEIRA_INVALIDA,
            listOf(
                parametroInvalido
            )
        )
    }

    @ExceptionHandler(PropostaInvalidaException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun propostaInvalida(ex: PropostaInvalidaException): Erro {

        val parametroInvalido = ParametroInvalido()
        parametroInvalido.nome = "id"
        parametroInvalido.descricao = ex.message

        return ErroFactory.criar(
            HttpStatus.NOT_FOUND,
            ID_PROPOSTA_INVALIDO,
            listOf(
                parametroInvalido
            )
        )
    }

    @ExceptionHandler(RemetenteException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun remetenteInvalido(ex: RemetenteException): Erro {

        val parametroInvalido = ParametroInvalido()
        parametroInvalido.nome = "usuário logado"
        parametroInvalido.descricao = ex.message

        return ErroFactory.criar(
            HttpStatus.BAD_REQUEST,
            REMETENTE_INVALIDO,
            listOf(
                parametroInvalido
            )
        )
    }
}
