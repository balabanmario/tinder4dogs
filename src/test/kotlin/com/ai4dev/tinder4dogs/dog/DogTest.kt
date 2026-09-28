package com.ai4dev.tinder4dogs.dog

import jakarta.persistence.Column
import jakarta.persistence.Transient
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DogTest {

    @Test
    fun `profile retains physical behavioral and optional mating fields`() {
        val ownerId = UUID.randomUUID()
        val dog = Dog(
            ownerId = ownerId,
            name = "Milo",
            breed = "Labrador",
            age = 4,
            size = "LARGE",
            sex = Gender.MALE,
            energyLevel = "HIGH",
            temperament = "Gentle and playful",
            matingMode = true,
            pedigree = "Kennel Club",
            neuteringStatus = "NOT_NEUTERED",
            healthStatus = "Healthy, no known conditions",
            profileComplete = true,
            missingFields = emptySet(),
            preferences = mutableSetOf("PLAY", "MATING"),
        )

        assertThat(dog.ownerId).isEqualTo(ownerId)
        assertThat(dog.name).isEqualTo("Milo")
        assertThat(dog.breed).isEqualTo("Labrador")
        assertThat(dog.preferences).containsExactlyInAnyOrder("PLAY", "MATING")
        assertThat(dog.size).isEqualTo("LARGE")
        assertThat(dog.sex).isEqualTo(Gender.MALE)
        assertThat(dog.gender).isEqualTo(Gender.MALE)
        assertThat(dog.energyLevel).isEqualTo("HIGH")
        assertThat(dog.temperament).isEqualTo("Gentle and playful")
        assertThat(dog.matingMode).isTrue()
        assertThat(dog.pedigree).isEqualTo("Kennel Club")
        assertThat(dog.neuteringStatus).isEqualTo("NOT_NEUTERED")
        assertThat(dog.healthStatus).isEqualTo("Healthy, no known conditions")
        assertThat(dog.profileComplete).isTrue()
        assertThat(dog.missingFields).isEmpty()
        assertThat(Dog::class.java.getDeclaredField("profileComplete").getAnnotation(Column::class.java)).isNotNull()
        assertThat(Dog::class.java.getDeclaredField("missingFields").getAnnotation(Column::class.java)).isNotNull()
        assertThat(Dog::class.java.getDeclaredField("profileComplete").getAnnotation(Transient::class.java)).isNull()
        assertThat(Dog::class.java.getDeclaredField("missingFields").getAnnotation(Transient::class.java)).isNull()
    }

    @Test
    fun `mating declarations have no verified status`() {
        val dog = Dog(
            name = "Luna",
            breed = "Beagle",
            sex = Gender.FEMALE,
            age = 3,
            matingMode = true,
            pedigree = "Owner declaration",
            healthStatus = "Owner declaration",
        )

        assertThat(dog.pedigree).isEqualTo("Owner declaration")
        assertThat(dog.healthStatus).isEqualTo("Owner declaration")
        assertThat(Dog::class.members.none { it.name == "verified" }).isTrue()
    }
}
