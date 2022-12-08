package com.trendyol.gpapi.domain.exception

import org.apache.commons.lang3.StringUtils

open class DomainException(vararg val messages: String) :
    RuntimeException() {
    override val message: String?
        get() = "Business exception occurred, " + StringUtils.join(messages, " ")
}
