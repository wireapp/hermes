package com.wire.utils

import dev.forst.katlib.InstantProvider
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID

// TODO replace this with some reasonably good library
fun randomString(): String = UUID.randomUUID().toString()

fun mockedNowProvider(now: Instant = Instant.now()): InstantProvider {
    val m = mockk<InstantProvider>()
    every { m.now() } returns now
    return m
}
