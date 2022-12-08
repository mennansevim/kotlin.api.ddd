package com.trendyol.gpapi.infrastructure.api.exception

import com.trendyol.gpapi.domain.exception.DomainException
import com.trendyol.gpapi.infrastructure.api.response.ErrorDetailResponse
import com.trendyol.gpapi.infrastructure.api.response.ErrorResponse
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import java.util.*
import java.util.function.Supplier

@RestControllerAdvice
class GlobalExceptionHandler(private val messageSource: MessageSource) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(java.lang.Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exception = "Exception")
        errorResponse.addError(ErrorDetailResponse(message = exception.message ?: "Unexpected"))
        logger.error(
            "Unexpected exception occurred. Caused By:{}, stackTrace: {}",
            errorResponse,
            ExceptionUtils.getStackTrace(exception)
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exception = "MethodArgumentNotValidException")
        logger.error("Field validation failed. Caused By:", exception)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIIllegalArgumentException(exception: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exception = "IllegalArgumentException")
        errorResponse.addError(ErrorDetailResponse(message = exception.message!!))
        logger.error("IllegalArgumentException Caused by:", exception)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exception = "BindException")
        errorResponse.addError(ErrorDetailResponse(key = getKey(exception), message = exception.message))
        logger.error("BindException Caused by:", exception)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleBindException(exception: ServerWebInputException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exception = "ServerWebInputException")
        errorResponse.addError(ErrorDetailResponse(message = exception.rootCause?.message ?: "Unexpected"))
        logger.error("Field validation failed. Caused By:", exception.rootCause)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(exception: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        val errorDetailResponses = exception.bindingResult.fieldErrors
            .map { error: FieldError ->
                ErrorDetailResponse(
                    message = getMessage(
                        error.defaultMessage,
                        listOf(error.field),
                        error.defaultMessage!!
                    ), key = error.field
                )
            }.toMutableList()

        return ResponseEntity(ErrorResponse(errorDetailResponses), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(exception: DomainException): ResponseEntity<ErrorDetailResponse> {
        val errorResponse = ErrorDetailResponse(
            message = exception.message ?: "", key = "domain.exception"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun getKey(exception: BindException): String {
        val errors = exception.bindingResult.allErrors
        if (errors.isNotEmpty()) return errors.joinToString(",") { it.defaultMessage ?: "" }
        return ""
    }

    private fun getMessage(key: String?, args: List<String>, defaultMessage: String): String {
        return Optional.of(getMessage(Supplier { messageSource.getMessage(key!!, args.toTypedArray(), Locale("en")) }))
            .filter { cs: String? -> cs!!.isNotBlank() }
            .orElseGet { defaultMessage }
    }

    private fun getMessage(supplier: Supplier<String>): String {
        return try {
            supplier.get()
        } catch (e: Exception) {
            logger.warn("Exception occurred : ", e)
            ""
        }
    }

}
