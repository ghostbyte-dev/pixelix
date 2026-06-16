package com.daniebeler.pfpixelix.domain.service.general

interface DtoMappable<T> {
    fun toDomain(): T
}