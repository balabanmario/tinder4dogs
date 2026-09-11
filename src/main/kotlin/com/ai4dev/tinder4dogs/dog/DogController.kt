package com.ai4dev.tinder4dogs.dog

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

data class DogRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val breed: String,
    val gender: Gender,
    @field:Min(0) val age: Int,
    val preferences: Set<String> = emptySet(),
)

data class DogResponse(
    val id: Long,
    val name: String,
    val breed: String,
    val gender: Gender,
    val age: Int,
    val preferences: Set<String>,
) {
    companion object {
        fun of(dog: Dog) = DogResponse(
            id = requireNonNullId(dog),
            name = dog.name,
            breed = dog.breed,
            gender = dog.gender,
            age = dog.age,
            preferences = dog.preferences.toSet(),
        )

        private fun requireNonNullId(dog: Dog): Long =
            dog.id ?: error("a persisted dog always has an id")
    }
}

@RestController
@RequestMapping("/api/dogs")
class DogController(
    private val dogs: DogRepository,
) {

    @GetMapping
    fun all(): List<DogResponse> = dogs.findAll().map(DogResponse::of)

    @GetMapping("/{id}")
    fun byId(@PathVariable id: Long): ResponseEntity<DogResponse> =
        dogs.findById(id)
            .map { ResponseEntity.ok(DogResponse.of(it)) }
            .orElseGet { ResponseEntity.notFound().build() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: DogRequest): DogResponse {
        val dog = Dog(
            name = request.name,
            breed = request.breed,
            gender = request.gender,
            age = request.age,
            preferences = request.preferences.toMutableSet(),
        )
        return DogResponse.of(dogs.save(dog))
    }
}
