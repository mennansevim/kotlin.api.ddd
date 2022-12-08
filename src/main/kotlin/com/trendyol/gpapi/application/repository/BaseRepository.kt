package com.trendyol.gpapi.application.repository

import com.trendyol.gpapi.domain.entity.Entity

interface BaseRepository<TEntity : Entity> {
    suspend fun save(entity: Entity)
    suspend fun findById(id : String) : TEntity?
}
