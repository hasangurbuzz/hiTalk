package dev.hasangurbuz.hitalk.remote.mapper

interface Mapper<E, D> {
    fun toEntity( dto: D): E

    fun toDto(entity: E): D
}