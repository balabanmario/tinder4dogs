package com.ai4dev.tinder4dogs.match

import com.ai4dev.tinder4dogs.dog.Dog
import com.ai4dev.tinder4dogs.dog.Gender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MatchScoreServiceTest {

    private val service = MatchScoreService()

    private fun dog(
        name: String,
        breed: String = "Labrador",
        gender: Gender = Gender.FEMALE,
        age: Int = 5,
        preferences: Set<String> = setOf("parks", "fetch"),
    ) = Dog(
        id = null,
        name = name,
        breed = breed,
        gender = gender,
        age = age,
        preferences = preferences.toMutableSet(),
    )

    private val rex = dog("Rex", gender = Gender.MALE, age = 4)

    @Test
    fun `two dogs produce a score`() {
        val bella = dog("Bella")

        assertThat(service.score(rex, bella)).isGreaterThan(0.0)
    }

    @Test
    fun `dogs of different gender score higher than dogs of the same gender`() {
        val bella = dog("Bella", gender = Gender.FEMALE)
        val max = dog("Max", gender = Gender.MALE)

        assertThat(service.score(rex, bella)).isGreaterThan(service.score(rex, max))
    }

    @Test
    fun `more shared preferences score higher than fewer`() {
        val bella = dog("Bella", preferences = setOf("parks", "fetch"))
        val ace = dog("Ace", preferences = setOf("parks"))

        assertThat(service.score(rex, bella)).isGreaterThan(service.score(rex, ace))
    }
}
