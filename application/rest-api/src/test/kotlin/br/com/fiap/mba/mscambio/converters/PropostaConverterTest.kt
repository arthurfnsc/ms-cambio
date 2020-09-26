package br.com.fiap.mba.mscambio.converters

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openapi.cambio.server.model.PropostaNegociacaoRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal

@ContextConfiguration(
    classes = [
        PropostaConverterImpl::class
    ]
)
@SpringBootTest
internal class PropostaConverterTest {

    @Autowired
    private lateinit var conversor: PropostaConverter

    @Test
    fun `validar injeção de dependência`() {
        assertThat(this.conversor).isNotNull
    }

    @Test
    fun `validar conversão PropostaNegociacaoRequest Dólar`() {

        val request = PropostaNegociacaoRequest()
        request.cotacaoReal = 1.0
        request.instituicaoFinanceira = "instituicaoFinanceira"
        request.nomeMoeda = PropostaNegociacaoRequest.NomeMoedaEnum.USD
        request.quantidadeMoeda = 2
        request.taxaPretendida = 3.0

        val resultado = this.conversor.from(request)

        assertThat(resultado.cotacaoReal.stripTrailingZeros()).isEqualTo(BigDecimal(1.0).stripTrailingZeros())
        assertThat(resultado.instituicaoFinanceira).isEqualTo("instituicaoFinanceira")
        assertThat(resultado.moeda).isNotEmpty.isEqualTo("USD")
        assertThat(resultado.quantidade).isEqualTo(2)
        assertThat(resultado.taxa.stripTrailingZeros()).isEqualTo(BigDecimal(3).stripTrailingZeros())
    }

    @Test
    fun `validar conversão PropostaNegociacaoRequest Euro`() {

        val request = PropostaNegociacaoRequest()
        request.cotacaoReal = 1.0
        request.instituicaoFinanceira = "instituicaoFinanceira"
        request.nomeMoeda = PropostaNegociacaoRequest.NomeMoedaEnum.EUR
        request.quantidadeMoeda = 2
        request.taxaPretendida = 3.0

        val resultado = this.conversor.from(request)

        assertThat(resultado.cotacaoReal.stripTrailingZeros()).isEqualTo(BigDecimal(1.0).stripTrailingZeros())
        assertThat(resultado.instituicaoFinanceira).isEqualTo("instituicaoFinanceira")
        assertThat(resultado.moeda).isNotEmpty.isEqualTo("EUR")
        assertThat(resultado.quantidade).isEqualTo(2)
        assertThat(resultado.taxa.stripTrailingZeros()).isEqualTo(BigDecimal(3).stripTrailingZeros())
    }
}
