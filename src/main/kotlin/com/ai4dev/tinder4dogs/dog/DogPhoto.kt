package com.ai4dev.tinder4dogs.dog

import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "dog_photo")
class DogPhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "dog_id", nullable = false)
    var dogId: Long? = null,

    /**
     * The bytes themselves, fetched only when something asks for them.
     *
     * [Basic] with [FetchType.LAZY] is what keeps a profile read from dragging
     * every photo into memory; the binary is loaded by the photo endpoint and
     * nowhere else.
     */
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "content", nullable = false)
    var content: ByteArray = ByteArray(0),

    @Column(name = "media_type", nullable = false)
    var mediaType: String = "",

    @Column(name = "original_filename", nullable = false)
    var originalFilename: String = "",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),
)

/**
 * A photo as the profile surface sees it: everything except the bytes.
 *
 * Spring Data projects straight onto this interface, so the `content` column
 * is never part of the query that builds profile responses.
 */
interface DogPhotoMetadata {
    val id: Long
    val mediaType: String
    val originalFilename: String
    val sortOrder: Int
}
