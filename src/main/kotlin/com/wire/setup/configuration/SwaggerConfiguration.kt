package com.wire.setup.configuration

import com.papsign.ktor.openapigen.model.info.InfoModel

data class SwaggerConfiguration(
    /**
     * Expose openapi.json.
     */
    val enableOpenApiJson: Boolean,
    /**
     * Expose /swagger-ui
     */
    val enableSwaggerUI: Boolean,
    /**
     * Swagger information as displayed in the swagger.
     */
    val info: InfoModel
)
