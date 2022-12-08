package com.trendyol.gpapi.infrastructure.couchbase.configuration

import com.couchbase.client.core.cnc.tracing.NoopRequestTracer
import com.couchbase.client.core.env.CompressionConfig
import com.couchbase.client.core.env.IoEnvironment
import com.couchbase.client.core.env.OrphanReporterConfig
import com.couchbase.client.core.env.TimeoutConfig
import com.couchbase.client.kotlin.Cluster
import com.couchbase.client.kotlin.env.ClusterEnvironment
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableConfigurationProperties(CouchbaseConfigurationProperties::class)
class CouchbaseConfiguration(
    private val couchbaseProperties: CouchbaseConfigurationProperties,
) {

    @Bean
    fun cluster(): Cluster {

        val clusterEnvironment = ClusterEnvironment.builder()
            .ioEnvironment(IoEnvironment.builder().eventLoopThreadCount(Runtime.getRuntime().availableProcessors()))
            .compressionConfig(CompressionConfig.builder().enable(true)).requestTracer(NoopRequestTracer.INSTANCE)
            .orphanReporterConfig(OrphanReporterConfig.builder().emitInterval(Duration.ofSeconds(60))).timeoutConfig(
                TimeoutConfig.builder().kvTimeout(couchbaseProperties.connection.kvTimeout)
                    .connectTimeout(couchbaseProperties.connection.connectTimeout)
                    .queryTimeout(couchbaseProperties.connection.queryTimeout)
            )

        return Cluster.connect(
            connectionString = couchbaseProperties.hosts.joinToString(","),
            username = couchbaseProperties.secrets.cbUsername!!,
            password = couchbaseProperties.secrets.cbPassword!!,
            clusterEnvironment
        )
    }
}
