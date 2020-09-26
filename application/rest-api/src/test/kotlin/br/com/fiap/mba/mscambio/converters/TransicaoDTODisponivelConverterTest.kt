package br.com.fiap.mba.mscambio.converters

import br.com.fiap.mba.mscambio.dtos.TransicaoDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openapi.cambio.server.model.TransicaoDisponivel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        TransicaoDisponivelConverterImpl::class
    ]
)
@SpringBootTest
internal class TransicaoDTODisponivelConverterTest {

    @Autowired
    private lateinit var conversor: TransicaoDisponivelConverter

    @Test
    fun `validar injeção de dependência`() {
        assertThat(this.conversor).isNotNull
    }

    @Test
    fun `validar conversão transição ACEITADO`() {

        val resultado = this.conversor.from(TransicaoDisponivel.TransicaoEnum.ACEITADO)

        assertThat(resultado).isEqualTo(TransicaoDTO.ACEITADO)
    }

    @Test
    fun `validar conversão transição CONTRA_PROPOSTA`() {

        val resultado = this.conversor.from(TransicaoDisponivel.TransicaoEnum.CONTRA_PROPOSTA)

        assertThat(resultado).isEqualTo(TransicaoDTO.CONTRA_PROPOSTA)
    }

    @Test
    fun `validar conversão transição REJEITADO`() {

        val resultado = this.conversor.from(TransicaoDisponivel.TransicaoEnum.REJEITADO)

        assertThat(resultado).isEqualTo(TransicaoDTO.REJEITADO)
    }
}
