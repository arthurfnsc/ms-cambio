package br.com.fiap.mba.mscambio.configs

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import springfox.documentation.spring.web.plugins.Docket

@ContextConfiguration(
    classes = [OpenAPIConfig::class]
)
@SpringBootTest
class OpenAPIConfigTest {

    @Autowired
    private lateinit var openAPIConfig: OpenAPIConfig

    @Autowired
    private lateinit var docket: Docket

    @Test
    fun `validar injeção de dependência | Docket`() {
        assertNotNull(this.docket)
    }

    @Test
    fun `validar injeção de dependência | OpenAPIConfig`() {
        assertNotNull(this.openAPIConfig)
    }
}
