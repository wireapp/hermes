package com.wire.service

import com.wire.dao.Database
import com.wire.dao.Domain
import com.wire.dao.Handle
import com.wire.dao.QualifiedId
import com.wire.dao.UserId
import com.wire.dao.model.Users
import com.wire.dto.user.LegalHoldStatusResponse
import com.wire.dto.user.UserDTO
import com.wire.dto.user.UserProfileDTO
import com.wire.extensions.getOne
import com.wire.extensions.selectForDomain
import com.wire.extensions.toQualifiedId
import com.wire.setup.configuration.FederationConfiguration
import dev.forst.katlib.mapToSet
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.andWhere

class UserService(
    private val db: Database,
    private val fc: FederationConfiguration
) {

    /**
     * Returns Qualified ID and the password hash for the given email.
     * If the user exits.
     */
    suspend fun getUsersPasswordHash(email: String): Pair<QualifiedId, String>? = db.query {
        userBy(fc.domain) { Users.email eq email }
            ?.let {
                Pair(it.toQualifiedId(Users), it[Users.passwordHash])
            }
    }

    // -- User DTO

    suspend fun getUserById(id: UserId): UserDTO? = db.query {
        userBy(id.domain) { Users.id eq id.id }
            ?.toUserDto()
    }

    // -- User Profiles

    suspend fun getUserProfileByHandle(handle: Handle): UserProfileDTO? = db.query {
        userBy(handle.domain) { Users.handle eq handle.handle }
            ?.toUserProfileDto()
    }

    suspend fun getUserProfilesByIds(ids: Collection<UserId>): List<UserProfileDTO> {
        // TODO: handle other servers
        val thisServerIds = ids.mapToSet { it.id }

        return db.query {
            usersBy(fc.domain) { Users.id inList thisServerIds }
        }
    }

    suspend fun getUserProfilesByHandles(handles: Collection<Handle>): List<UserProfileDTO> {
        // TODO: handle other servers
        val thisServerHandles = handles.mapToSet { it.handle }

        return db.query {
            usersBy(fc.domain) { Users.handle inList thisServerHandles }
        }
    }

    private fun userBy(domain: Domain, where: SqlExpressionBuilder.() -> Op<Boolean>) =
        Users.selectForDomain(domain)
            .andWhere(where)
            .getOne()

    private fun usersBy(domain: Domain, where: SqlExpressionBuilder.() -> Op<Boolean>) =
        Users.selectForDomain(domain)
            .andWhere(where)
            .map { it.toUserProfileDto() }
}

// TODO: we don't store all data right now, so these are default values

private fun ResultRow.toUserProfileDto() = Users.toProfileDto(this)
private fun Users.toProfileDto(row: ResultRow) = UserProfileDTO(
    id = QualifiedId(row[domain], row[id]),
    name = row[handle],
    handle = row[handle],
    legalHoldStatus = LegalHoldStatusResponse.DISABLED,
    teamId = row[teamId],
    accentId = 0,
    assets = listOf(),
    deleted = row[deleted] == null,
    email = row[email],
    expiresAt = null,
    nonQualifiedId = row[id],
    service = null
)


private fun ResultRow.toUserDto() = Users.toDto(this)
private fun Users.toDto(row: ResultRow) = UserDTO(
    id = QualifiedId(row[domain], row[id]),
    name = row[handle],
    handle = row[handle],
    teamId = row[teamId],
    accentId = 0,
    assets = listOf(),
    deleted = row[deleted] == null,
    email = row[email],
    expiresAt = null,
    nonQualifiedId = row[id],
    service = null,
    locale = "en-us"
)
