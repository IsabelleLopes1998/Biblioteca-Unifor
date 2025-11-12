package com.example.bbunifor.datasource.entity

data class BookApiResponse(
    val kind: String?,
    val totalItems: Int?,
    val items: List<VolumeItem>?
)
