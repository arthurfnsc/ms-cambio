package br.com.fiap.mba.mscambio.configs

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
open class I18nConfig {

    @Bean
    open fun validator(): LocalValidatorFactoryBean {
        val factory = LocalValidatorFactoryBean()
        factory.setValidationMessageSource(messageSource())

        return factory
    }

    @Bean
    open fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:i18n/messages")
        messageSource.setDefaultEncoding("UTF-8")

        return messageSource
    }
}
