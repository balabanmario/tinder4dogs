package com.ai4dev.tinder4dogs.match

import com.ai4dev.tinder4dogs.dog.Dog
import com.ai4dev.tinder4dogs.dog.DogRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class MatchResponse(
    val dogId: Long,
    val name: String,
    val score: Double,
)

@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val dogs: DogRepository,
    private val scores: MatchScoreService,
) {

    @GetMapping("/{id}")
    fun bestFor(@PathVariable id: Long): ResponseEntity<List<MatchResponse>> {
        val subject = dogs.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val ranked = dogs.findAll()
            .filter { it.id != subject.id }
            .map { candidate -> toResponse(candidate, scores.score(subject, candidate)) }
            .sortedByDescending { it.score }

        return ResponseEntity.ok(ranked)
    }

    @GetMapping("/{aId}/{bId}")
    fun between(@PathVariable aId: Long, @PathVariable bId: Long): ResponseEntity<MatchResponse> {
        val a = dogs.findById(aId).orElse(null) ?: return ResponseEntity.notFound().build()
        val b = dogs.findById(bId).orElse(null) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(toResponse(b, scores.score(a, b)))
    }

    private fun toResponse(dog: Dog, score: Double) = MatchResponse(
        dogId = dog.id ?: error("a persisted dog always has an id"),
        name = dog.name,
        score = score,
    )
}
