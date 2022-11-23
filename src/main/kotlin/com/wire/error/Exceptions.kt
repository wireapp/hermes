package com.wire.error

import kotlin.reflect.jvm.jvmName

sealed class ApplicationException(
    override val message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause)

inline fun <reified T : Any> EntityNotFoundException(query: String) =
    EntityNotFoundException(query, T::class.simpleName ?: T::class.qualifiedName ?: T::class.jvmName)

data class EntityNotFoundException(
    val searchQuery: String,
    val entityName: String
) : ApplicationException("Entity $entityName not found by query: $searchQuery.")


class AuthenticationFailedException : ApplicationException("Authentication failed!")
