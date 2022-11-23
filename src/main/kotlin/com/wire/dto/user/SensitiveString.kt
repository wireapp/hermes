package com.wire.dto.user

typealias Password = SensitiveString

@JvmInline
value class SensitiveString(val value: String) {

    operator fun invoke() = value

    override fun toString(): String {
        return "*****"
    }
}
