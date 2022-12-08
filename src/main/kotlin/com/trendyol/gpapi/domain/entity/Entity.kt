package com.trendyol.gpapi.domain.entity

import com.trendyol.gpapi.domain.exception.DomainException
import com.trendyol.gpapi.infrastructure.util.Clock

open class Entity(open val id: String) {
    var status: EntityStatus = EntityStatus.Active
    var version: Long? = null
        set(value) {
            version.let {
                field = value
            }
        }
    var creationDate: Long? = Clock.nowEpoch()
    var lastModifiedDate: Long? = Clock.nowEpoch()

    fun checkActive(): Boolean {
        return status == EntityStatus.Active
    }

    fun active() {
        if (status == EntityStatus.Deleted) {
            throw DomainException("Entity status must not be deleted.")
        }

        status = EntityStatus.Active;
    }

    fun passive() {
        if (status == EntityStatus.Deleted) {
            throw DomainException("Entity status must not be deleted.")
        }

        status = EntityStatus.Passive;
    }

    fun delete() {
        status = EntityStatus.Deleted;
    }
}
