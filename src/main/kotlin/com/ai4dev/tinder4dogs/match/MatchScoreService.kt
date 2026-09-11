package com.ai4dev.tinder4dogs.match

import com.ai4dev.tinder4dogs.dog.Dog
import org.springframework.stereotype.Service

@Service
class MatchScoreService {

    fun score(a: Dog, b: Dog): Double {
        var score = 0.0

        val ageGap = a.age - b.age
        score += (10 - ageGap) * 2.0

        if (a.breed == b.breed) score + 25.0

        if (a.gender != b.gender) score += 20.0

        val shared = a.preferences.count { b.preferences.contains(it) }
        score += shared * 5

        return score / 100
    }
}
