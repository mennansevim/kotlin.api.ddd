package com.trendyol.gpapi.infrastructure.couchbase

import com.couchbase.client.kotlin.kv.GetResult
import com.couchbase.client.kotlin.query.QueryRow
import com.newrelic.api.agent.DatastoreParameters
import com.newrelic.api.agent.NewRelic
import com.newrelic.api.agent.Trace
import com.trendyol.gpapi.application.repository.BaseRepository
import com.trendyol.gpapi.domain.entity.Entity
import com.trendyol.gpapi.domain.entity.EntityStatus
import com.trendyol.gpapi.infrastructure.util.Clock

abstract class BaseRepositoryImpl<T : Entity>(private val collection: com.couchbase.client.kotlin.Collection) :
    BaseRepository<T> {

    abstract fun map(result: GetResult): T

    abstract fun map(row: QueryRow): T

    @Trace(metricName = "BaseRepositoryImpl.save")
    override suspend fun save(entity: Entity) {
        entity.lastModifiedDate = Clock.nowEpoch()
        entity.version?.let {
            collection.replace(id = entity.id, content = entity, cas = entity.version!!)
                .also { tracedExternalSource("replace") }
        } ?: collection.insert(entity.id, entity)
            .also { tracedExternalSource("insert") }
    }

    @Trace(metricName = "BaseRepositoryImpl.findById")
    override suspend fun findById(id: String): T? {
        return collection.getOrNull(id)
            .also { tracedExternalSource("get") }
            ?.let { result ->

                val entity = map(result)

                if (entity.status == EntityStatus.Deleted) {
                    return null
                }

                entity.version = result.cas
                return entity
            }
    }

    private fun tracedExternalSource(operation: String) {
        NewRelic.getAgent().tracedMethod.reportAsExternal(
            DatastoreParameters
                .product("Couchbase")
                .collection(collection.name)
                .operation(operation)
                .noInstance()
                .databaseName(collection.scope.name)
                .build()
        )
    }
}
