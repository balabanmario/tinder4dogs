package com.ai4dev.tinder4dogs.match

import com.ai4dev.tinder4dogs.dog.Dog
import com.ai4dev.tinder4dogs.dog.Gender
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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

    // ── the tests the repository shipped with ────────────────────────────────
    //
    // Every one of them passed against the broken implementation too. That is
    // the point: they pin relations, and all four defects preserved every
    // relation they pin. Coverage is not the same as constraint.

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

    // ── one test per defect, each of which fails against the old code ────────

    @Test
    fun `the same breed scores higher than a different breed`() {
        val sameBreed = dog("Bella", breed = "Labrador")
        val otherBreed = dog("Luna", breed = "Beagle")

        assertThat(service.score(rex, sameBreed))
            .isGreaterThan(service.score(rex, otherBreed))
    }

    @Test
    fun `age distance counts the same in both directions`() {
        val threeYearsYounger = dog("Pip", age = rex.age - 3)
        val threeYearsOlder = dog("Duke", age = rex.age + 3)

        assertThat(service.score(rex, threeYearsYounger))
            .isEqualTo(service.score(rex, threeYearsOlder))
    }

    @Test
    fun `a much older dog never scores higher than a close one`() {
        val closeInAge = dog("Bella", age = 5)
        val muchOlder = dog("Nonna", age = 20)

        assertThat(service.score(rex, muchOlder))
            .isLessThan(service.score(rex, closeInAge))
    }

    @Test
    fun `the score stays within zero and one, whatever the input`() {
        val perfect = dog(
            "Perfect",
            age = rex.age,
            preferences = setOf("parks", "fetch", "naps", "cars", "food", "sofa"),
        )
        val opposite = dog("Opposite", breed = "Beagle", gender = Gender.MALE, age = 15)

        assertThat(service.score(rex, perfect)).isBetween(0.0, 1.0)
        assertThat(service.score(rex, opposite)).isBetween(0.0, 1.0)
    }

    @Test
    fun `a negative age is rejected rather than scored`() {
        val impossible = dog("Impossible", age = -3)

        assertThatThrownBy { service.score(rex, impossible) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("age cannot be negative")
    }

    // ── Module 4: the question a caller has to ask before it asks for a score ──

    @Test
    fun `a dog with a negative age cannot be scored`() {
        assertThat(service.canScore(dog("Nonna", age = -3))).isFalse()
    }

    @Test
    fun `a dog with a plausible age can be scored`() {
        assertThat(service.canScore(rex)).isTrue()
    }
}
