package com.trendyol.gpapi.infrastructure.couchbase.configuration

import com.trendyol.gpapi.infrastructure.config.SecretConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "couchbase")
data class CouchbaseConfigurationProperties(
    var hosts: List<String> = listOf(),
    var connection: CouchbaseConnection = CouchbaseConnection(),
    var bucket: String = "",
    var scope: String = "",
    val secrets: SecretConfiguration
)

data class CouchbaseConnection(
    var connectTimeout: Duration = Duration.ofSeconds(15),
    var kvTimeout: Duration = Duration.ofSeconds(1),
    var queryTimeout: Duration = Duration.ofSeconds(5)
)
