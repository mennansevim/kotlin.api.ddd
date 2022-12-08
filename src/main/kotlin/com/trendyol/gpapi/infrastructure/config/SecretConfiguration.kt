package com.trendyol.gpapi.infrastructure.config

import com.trendyol.dynamic.configuration.processor.DynamicConfigurationProperties
import com.trendyol.dynamic.configuration.processor.EnableDynamicConfiguration
import org.springframework.context.annotation.Configuration

@DynamicConfigurationProperties(value = "./config/secrets.json")
@Configuration
@EnableDynamicConfiguration
data class SecretConfiguration(
    var cbUsername: String? = null,
    var cbPassword: String? = null
)