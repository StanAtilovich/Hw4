package com.example.hw4.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostRemoveKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val key: Long
) {
    enum class KeyType {
        AFTER,
        BEFORE,
    }

}
