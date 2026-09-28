package com.ai4dev.tinder4dogs.dog

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

enum class Gender {
    MALE,
    FEMALE,
}

@Entity
@Table(name = "dog")
class Dog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "owner_id", nullable = false, unique = true)
    var ownerId: UUID? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var breed: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sex: Gender = Gender.FEMALE,

    @Column(nullable = false)
    var age: Int = 0,

    @Column(nullable = false)
    var size: String = "UNKNOWN",

    @Column(name = "energy_level", nullable = false)
    var energyLevel: String = "UNKNOWN",

    @Column(nullable = false)
    var temperament: String = "",

    @Column(name = "mating_mode", nullable = false)
    var matingMode: Boolean = false,

    var pedigree: String? = null,

    @Column(name = "neutering_status")
    var neuteringStatus: String? = null,

    @Column(name = "health_status")
    var healthStatus: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "creation_duration_ms", nullable = false)
    var creationDurationMs: Long = 0,

    @Column(name = "profile_complete", nullable = false)
    var profileComplete: Boolean = false,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "missing_fields", nullable = false, columnDefinition = "text[]")
    var missingFields: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "dog_preference",
        joinColumns = [JoinColumn(name = "dog_id")],
    )
    @Column(name = "preference", nullable = false)
    var preferences: MutableSet<String> = mutableSetOf(),
) {
    /**
     * The gender-based name the `match` package scores on.
     *
     * [sex] is the profile field; this alias keeps `match` reading the same
     * semantics it always did, so scoring needs no change. It has no backing
     * field, so Hibernate never maps it to a column.
     */
    var gender: Gender
        get() = sex
        set(value) {
            sex = value
        }

    /**
     * Builds a dog from the pre-profile surface, naming the gender field as
     * `match` and its tests already name it.
     *
     * [gender] has no default on purpose: it is what tells this constructor
     * apart from the primary one, whose parameters all default. Without that,
     * `Dog(name = "Rex")` would match both and fail to resolve.
     */
    constructor(
        gender: Gender,
        id: Long? = null,
        name: String = "",
        breed: String = "",
        age: Int = 0,
        preferences: MutableSet<String> = mutableSetOf(),
    ) : this(
        id = id,
        name = name,
        breed = breed,
        sex = gender,
        age = age,
        preferences = preferences,
    )
}
