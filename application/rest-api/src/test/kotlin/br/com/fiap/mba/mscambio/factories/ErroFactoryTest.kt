package br.com.fiap.mba.mscambio.factories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openapi.cambio.server.model.ParametroInvalido
import org.springframework.http.HttpStatus

class ErroFactoryTest {

    @Test
    fun `validar conversão ErroFactory com HTTP Status`() {

        val erro = ErroFactory.criar(
            httpStatus = HttpStatus.OK
        )

        assertThat(erro.descricao).isNull()
        assertThat(erro.httpCode).isEqualTo(HttpStatus.OK.value())
        assertThat(erro.httpMessage).isEqualTo(HttpStatus.OK.reasonPhrase)
        assertThat(erro.parametrosInvalidos).isEmpty()
    }

    @Test
    fun `validar conversão ErroFactory com HTTP Status e descrição`() {

        val erro = ErroFactory.criar(
            httpStatus = HttpStatus.OK,
            descricao = "ABC"
        )

        assertThat(erro.descricao).isEqualTo("ABC")
        assertThat(erro.httpCode).isEqualTo(HttpStatus.OK.value())
        assertThat(erro.httpMessage).isEqualTo(HttpStatus.OK.reasonPhrase)
        assertThat(erro.parametrosInvalidos).isEmpty()
    }

    @Test
    fun `validar conversão ErroFactory com HTTP Status, descrição`() {

        val p1 = ParametroInvalido()
        p1.descricao = "DEF"
        p1.nome = "GHI"

        val p2 = ParametroInvalido()
        p1.descricao = "JKL"
        p1.nome = "MNO"

        val parametrosInvalidos = listOf(p1, p2)

        val erro = ErroFactory.criar(
            httpStatus = HttpStatus.OK,
            descricao = "ABC",
            parametrosInvalidos = parametrosInvalidos
        )

        assertThat(erro.descricao).isEqualTo("ABC")
        assertThat(erro.httpCode).isEqualTo(HttpStatus.OK.value())
        assertThat(erro.httpMessage).isEqualTo(HttpStatus.OK.reasonPhrase)
        assertThat(erro.parametrosInvalidos).hasSize(2)
        assertThat(erro.parametrosInvalidos).containsAll(parametrosInvalidos)
    }
}
