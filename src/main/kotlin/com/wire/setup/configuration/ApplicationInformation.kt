package com.wire.setup.configuration

import java.net.URL

/**
 * Information about the application.
 */
data class ApplicationInformation(
    /**
     * Version of the running code.
     */
    val version: String,
    /**
     * Environment for which was the build created.
     */
    val environment: Environment,
    /**
     * Application name.
     */
    val name: String,
    /**
     * Description of what does the application do.
     */
    val description: String,
    /**
     * URL where is application accessible.
     */
    val url: URL,
    /**
     * Information about the developer.
     */
    val publisher: PublisherInformation
) {
    @Suppress("unused")
    enum class Environment {
        /**
         * Developer runs code on local machine.
         */
        DEVELOPMENT,

        /**
         * Latest master running on staging.
         */
        STAGING,

        /**
         * Production ready code.
         */
        PRODUCTION,

        /**
         * When running unit tests.
         */
        TEST,

        /**
         * When showcasing this to someone.
         */
        DEMO
    }

    data class PublisherInformation(
        /**
         * Name of the company/developer.
         */
        val name: String,
        /**
         * URL of web.
         */
        val url: URL,
        /**
         * E-mail providing support for the application.
         */
        val email: String
    )
}
