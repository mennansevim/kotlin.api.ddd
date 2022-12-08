package com.trendyol.gpapi.infrastructure.config

import com.newrelic.api.agent.NewRelic
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*


@Configuration
class HeaderWebFilter : WebFilter {

    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {

        val correlationId = serverWebExchange.getCorrelationId()
        val agentName = serverWebExchange.getAgentName()
        MDC.put(CORRELATION_ID_KEY, correlationId)
        MDC.put(AGENT_NAME_KEY, agentName)
        MDC.put(REQUEST_PATH_KEY, serverWebExchange.getRequestPath())


        NewRelic.addCustomParameter(CORRELATION_ID_KEY, correlationId)
        NewRelic.addCustomParameter(AGENT_NAME_KEY, agentName)

        return webFilterChain.filter(serverWebExchange)
               .doFinally { MDC.clear() }
    }
}
const val CORRELATION_ID_KEY: String = "x-correlationid"
const val AGENT_NAME_KEY = "x-agentname"
const val REQUEST_PATH_KEY = "Request-Path"

fun ServerWebExchange.getCorrelationId(): String {
    return this.request.headers.toSingleValueMap().filter { it.key.equals(CORRELATION_ID_KEY, true) }
        .values.firstOrNull() ?: (UUID.randomUUID().toString() + "-" + System.currentTimeMillis())
}

fun ServerWebExchange.getAgentName(): String {
    return this.request.headers.toSingleValueMap().filter { it.key.equals(AGENT_NAME_KEY, true) }
        .values.firstOrNull() ?: "agent-not-found"
}

fun ServerWebExchange.getRequestPath(): String {
    return "${this.request.method?.name} ${this.request.path.value()}"
}