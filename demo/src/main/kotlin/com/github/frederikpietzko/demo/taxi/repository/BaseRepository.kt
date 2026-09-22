package com.github.frederikpietzko.demo.taxi.repository

interface BaseRepository<T> {
    fun findAll(): List<T>
    fun findById(id: Long): T?
    fun create(entity: T): T
    fun update(entity: T): T
    fun upsert(entity: T): T
    fun delete(entity: T): Boolean
    fun deleteById(id: Long): Boolean
}