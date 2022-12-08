package com.trendyol.gpapi.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType.SWAGGER_2
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    @Value("\${spring.application.name}")
    lateinit var applicationName: String


    @Bean
    fun stockApi(): Docket {
        return Docket(SWAGGER_2)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            .groupName(applicationName)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.trendyol"))
            .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title(applicationName)
            .description(applicationName)
            .version("1.0")
            .build()
    }

}