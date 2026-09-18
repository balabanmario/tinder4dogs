package com.ai4dev.tinder4dogs.match

import com.ai4dev.tinder4dogs.dog.Dog
import kotlin.math.abs
import org.springframework.stereotype.Service

/**
 * Scores how well two dogs match, on a scale from 0.0 to 1.0.
 *
 * Four things contribute, and each one is capped, which is what keeps the
 * result inside the scale it claims to be on:
 *
 *  - closeness in age, worth at most [AGE_MAX_POINTS]
 *  - the same breed, worth [BREED_POINTS]
 *  - opposite genders, worth [GENDER_POINTS]
 *  - shared preferences, worth [POINTS_PER_PREFERENCE] each, at most
 *    [PREFERENCE_MAX_POINTS] in total
 */
@Service
class MatchScoreService {

    fun score(a: Dog, b: Dog): Double {
        require(a.age >= 0) { "age cannot be negative: ${a.name} is ${a.age}" }
        require(b.age >= 0) { "age cannot be negative: ${b.name} is ${b.age}" }

        var points = agePoints(a.age, b.age)
        if (a.breed == b.breed) points += BREED_POINTS
        if (a.gender != b.gender) points += GENDER_POINTS
        points += preferencePoints(a, b)

        return (points / MAX_POINTS).coerceIn(0.0, 1.0)
    }

    /**
     * Whether this dog can be scored at all.
     *
     * [score] refuses a negative age, and it is right to: a negative age is not
     * a poor match, it is a row that should not exist. But a caller that reads
     * many dogs has to be able to ask the question *before* it asks for the
     * score. Without this, one impossible row takes the whole answer down with
     * it, which is exactly what `GET /api/matches/{id}` used to do.
     */
    fun canScore(dog: Dog): Boolean = dog.age >= 0

    /** Full marks at the same age, nothing from ten years apart onwards. */
    private fun agePoints(ageA: Int, ageB: Int): Double =
        ((MAX_AGE_GAP - abs(ageA - ageB)) * 2.0).coerceAtLeast(0.0)

    private fun preferencePoints(a: Dog, b: Dog): Double {
        val shared = a.preferences.count { b.preferences.contains(it) }
        return (shared * POINTS_PER_PREFERENCE).coerceAtMost(PREFERENCE_MAX_POINTS)
    }

    private companion object {
        const val MAX_AGE_GAP = 10
        const val AGE_MAX_POINTS = 20.0
        const val BREED_POINTS = 25.0
        const val GENDER_POINTS = 20.0
        const val POINTS_PER_PREFERENCE = 5.0
        const val PREFERENCE_MAX_POINTS = 15.0

        const val MAX_POINTS =
            AGE_MAX_POINTS + BREED_POINTS + GENDER_POINTS + PREFERENCE_MAX_POINTS
    }
}
