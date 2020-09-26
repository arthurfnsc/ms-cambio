package br.com.fiap.mba.mscambio.configs

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.test.context.ContextConfiguration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@ContextConfiguration(
    classes = [I18nConfig::class]
)
@SpringBootTest
class I18nConfigTest {

    @Autowired
    private lateinit var i18nConfig: I18nConfig

    @Autowired
    private lateinit var localValidatorFactory: LocalValidatorFactoryBean

    @Autowired
    private lateinit var messageSource: MessageSource

    @Test
    fun `validar injeção de dependência | I18nConfig`() {
        assertNotNull(this.i18nConfig)
    }

    @Test
    fun `validar injeção de dependência | LocalValidatorFactory`() {
        assertNotNull(this.localValidatorFactory)
    }

    @Test
    fun `validar injeção de dependência | MessageSource`() {
        assertNotNull(this.messageSource)
    }
}
