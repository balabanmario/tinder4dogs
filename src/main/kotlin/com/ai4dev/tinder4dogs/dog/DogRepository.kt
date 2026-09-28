package com.ai4dev.tinder4dogs.dog

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface DogRepository : JpaRepository<Dog, Long> {

    /** Detects an existing profile before one is created for the same owner. */
    fun findByOwnerId(ownerId: UUID): Dog?
}
