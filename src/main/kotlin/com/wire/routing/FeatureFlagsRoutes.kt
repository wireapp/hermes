package com.wire.routing

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tags
import com.wire.dto.OkResponse
import com.wire.extensions.closestDI
import com.wire.setup.configuration.ApplicationInformation
import org.kodein.di.instance

fun NormalOpenAPIRoute.featureFlagsRoutes() {
    route("${VERSION}/feature-configs").get<Unit, FeatureConfigResponse>(
        info(summary = "Returns feature flags."),
    ) {
        respond(
            FeatureConfigResponse(
                appLock = FeatureConfigData.AppLock(
                    AppLockConfigDTO(false, 80),
                    FeatureFlagStatusDTO.DISABLED,
                ),
                classifiedDomains = FeatureConfigData.ClassifiedDomains(
                    ClassifiedDomainsConfigDTO(emptyList()),
                    FeatureFlagStatusDTO.DISABLED
                ),
                conferenceCalling = FeatureConfigData.ConferenceCalling(
                    FeatureFlagStatusDTO.DISABLED
                ),
                conversationGuestLinks = FeatureConfigData.ConversationGuestLinks(
                    FeatureFlagStatusDTO.DISABLED
                ),
                digitalSignatures = FeatureConfigData.DigitalSignatures(
                    FeatureFlagStatusDTO.DISABLED
                ),
                fileSharing = FeatureConfigData.FileSharing(
                    FeatureFlagStatusDTO.DISABLED
                ),
                legalHold = FeatureConfigData.Legalhold(
                    FeatureFlagStatusDTO.DISABLED
                ),
                searchVisibility = FeatureConfigData.SearchVisibility(
                    FeatureFlagStatusDTO.DISABLED
                ),
                selfDeletingMessages = FeatureConfigData.SelfDeletingMessages(
                    SelfDeletingMessagesConfigDTO(10),
                    FeatureFlagStatusDTO.DISABLED
                ),
                sndFactorPasswordChallenge = FeatureConfigData.SecondFactorPasswordChallenge(
                    FeatureFlagStatusDTO.DISABLED
                ),
                sso = FeatureConfigData.SSO(
                    FeatureFlagStatusDTO.DISABLED
                ),
                validateSAMLEmails = FeatureConfigData.ValidateSAMLEmails(
                    FeatureFlagStatusDTO.DISABLED
                ),
                mls = FeatureConfigData.MLS(
                    MLSConfigDTO(
                        protocolToggleUsers = listOf(),
                        defaultProtocol = "mls",
                        allowedCipherSuites = listOf(1),
                        defaultCipherSuite = 1
                    ),
                    FeatureFlagStatusDTO.ENABLED
                )
            )
        )
    }
}


data class FeatureConfigResponse(
    @JsonProperty("appLock")
    val appLock: FeatureConfigData.AppLock,
    @JsonProperty("classifiedDomains")
    val classifiedDomains: FeatureConfigData.ClassifiedDomains,
    @JsonProperty("conferenceCalling")
    val conferenceCalling: FeatureConfigData.ConferenceCalling,
    @JsonProperty("conversationGuestLinks")
    val conversationGuestLinks: FeatureConfigData.ConversationGuestLinks,
    @JsonProperty("digitalSignatures")
    val digitalSignatures: FeatureConfigData.DigitalSignatures,
    @JsonProperty("fileSharing")
    val fileSharing: FeatureConfigData.FileSharing,
    @JsonProperty("legalhold")
    val legalHold: FeatureConfigData.Legalhold,
    @JsonProperty("searchVisibility")
    val searchVisibility: FeatureConfigData.SearchVisibility,
    @JsonProperty("selfDeletingMessages")
    val selfDeletingMessages: FeatureConfigData.SelfDeletingMessages,
    @JsonProperty("sndFactorPasswordChallenge")
    val sndFactorPasswordChallenge: FeatureConfigData.SecondFactorPasswordChallenge,
    @JsonProperty("sso")
    val sso: FeatureConfigData.SSO,
    @JsonProperty("validateSAMLemails")
    val validateSAMLEmails: FeatureConfigData.ValidateSAMLEmails,
    @JsonProperty("mls")
    val mls: FeatureConfigData.MLS?
)


enum class FeatureFlagStatusDTO {
    @JsonProperty("enabled")
    ENABLED,

    @JsonProperty("disabled")
    DISABLED;
}


data class AppLockConfigDTO(
    @JsonProperty("enforceAppLock")
    val enforceAppLock: Boolean,
    @JsonProperty("inactivityTimeoutSecs")
    val inactivityTimeoutSecs: Int
)


data class ClassifiedDomainsConfigDTO(
    @JsonProperty("domains")
    val domains: List<String>
)

data class MLSConfigDTO(
    @JsonProperty("protocolToggleUsers")
    val protocolToggleUsers: List<String>,
    @JsonProperty("defaultProtocol")
    val defaultProtocol: String, // proteus, mls
    @JsonProperty("allowedCipherSuites")
    val allowedCipherSuites: List<Int>,
    @JsonProperty("defaultCipherSuite")
    val defaultCipherSuite: Int
)


data class SelfDeletingMessagesConfigDTO(
    @JsonProperty("enforcedTimeoutSeconds")
    val enforcedTimeoutSeconds: Int
)

sealed class FeatureConfigData {
    data class Unknown(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class AppLock(
        @JsonProperty("config")
        val config: AppLockConfigDTO,
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class ClassifiedDomains(
        @JsonProperty("config")
        val config: ClassifiedDomainsConfigDTO,
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class ConferenceCalling(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class ConversationGuestLinks(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class DigitalSignatures(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class FileSharing(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class Legalhold(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class SearchVisibility(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class SelfDeletingMessages(
        @JsonProperty("config")
        val config: SelfDeletingMessagesConfigDTO,
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class SecondFactorPasswordChallenge(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class SSO(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class ValidateSAMLEmails(
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()

    data class MLS(
        @JsonProperty("config")
        val config: MLSConfigDTO,
        @JsonProperty("status")
        val status: FeatureFlagStatusDTO
    ) : FeatureConfigData()
}
