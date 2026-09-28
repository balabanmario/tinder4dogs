package com.ai4dev.tinder4dogs.dog

import org.springframework.data.jpa.repository.JpaRepository

interface DogPhotoRepository : JpaRepository<DogPhoto, Long> {

    /**
     * Scoped by both ids on purpose: asking for a photo by its own id alone
     * would hand out a photo that belongs to another dog.
     */
    fun findByIdAndDogId(id: Long, dogId: Long): DogPhoto?

    fun findByDogIdOrderBySortOrder(dogId: Long): List<DogPhotoMetadata>
}
