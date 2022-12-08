package com.trendyol.gpapi.infrastructure.api.response

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ToStringBuilder

class ErrorResponse {
    var exception: String = ""
    var errors = mutableListOf<ErrorDetailResponse>()

    constructor()

    constructor(exception: String = "", vararg exceptionMessage: String?) {
        if (StringUtils.isNotBlank(exception)) this.exception = exception

        if (!exceptionMessage.isNullOrEmpty()) {
            exceptionMessage.forEach {
                errors.add(ErrorDetailResponse(message = it!!))
            }
        }
    }

    constructor(errors: MutableList<ErrorDetailResponse>) {
        this.errors = errors
    }

    fun addError(errorDetailDTO: ErrorDetailResponse) {
        errors.add(errorDetailDTO)
    }

    override fun toString() = ToStringBuilder.reflectionToString(this)!!
}