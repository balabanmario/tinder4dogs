package com.ai4dev.tinder4dogs.dog

import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.AvailableSettings
import org.junit.jupiter.api.Test
import org.springframework.data.repository.query.parser.PartTree

class DogPhotoTest {

    @Test
    fun `dog photo maps binary content and metadata onto the migration columns`() {
        val migrationColumns = requireNotNull(
            javaClass.getResource("/db/changelog/changes/005-create-dog-profile.sql"),
        ).readText()
            .lineSequence()
            .flatMap { line ->
                Regex("^\\s*([a-z_]+)\\s+(?:BYTEA|VARCHAR|TEXT|INT|BIGINT|TIMESTAMPTZ)")
                    .findAll(line)
                    .map { it.groupValues[1] }
            }
            .toSet()

        withMetadata { metadata ->
            val photoMapping = metadata.getEntityBinding(DogPhoto::class.java.name)
            val mappedColumnsByProperty = photoMapping.propertyClosure.associate { property ->
                property.name to property.value.selectables.map { selectable -> selectable.text }
            }
            val expectedColumns = mapOf(
                "content" to "content",
                "mediaType" to "media_type",
                "originalFilename" to "original_filename",
                "sortOrder" to "sort_order",
                "createdAt" to "created_at",
                "dogId" to "dog_id",
            )

            assertThat(photoMapping.table.name)
                .isEqualTo(DogPhoto::class.java.getAnnotation(Table::class.java).name)
            expectedColumns.forEach { (property, column) ->
                assertThat(mappedColumnsByProperty[property]).containsExactly(column)
                assertThat(migrationColumns).contains(column)
            }
        }
    }

    @Test
    fun `a dog does not load its photos while loading the profile`() {
        val photos = Dog::class.java.getDeclaredField("photos")

        assertThat(photos.getAnnotation(OneToMany::class.java).fetch).isEqualTo(FetchType.LAZY)

        withMetadata { metadata ->
            val association = metadata.getEntityBinding(Dog::class.java.name).getProperty("photos")

            assertThat((association.value as org.hibernate.mapping.Collection).isLazy).isTrue()
        }
    }

    @Test
    fun `photo metadata never carries the binary content`() {
        val projectedProperties = DogPhotoMetadata::class.java.methods
            .map { it.name }
            .filter { it.startsWith("get") }
            .map { it.removePrefix("get").replaceFirstChar(Char::lowercaseChar) }
        val photoProperties = DogPhoto::class.java.declaredFields.map { it.name }

        assertThat(projectedProperties).isNotEmpty()
        assertThat(projectedProperties).doesNotContain("content")
        assertThat(photoProperties).contains("content")
        assertThat(photoProperties).containsAll(projectedProperties)
    }

    @Test
    fun `a photo is looked up by both its dog and its own id`() {
        val scopedLookup = PartTree("findByIdAndDogId", DogPhoto::class.java)

        assertThat(scopedLookup.parts.map { it.property.segment })
            .containsExactlyInAnyOrder("id", "dogId")
    }

    @Test
    fun `an existing profile is detected through its owner id`() {
        val ownerLookup = PartTree("findByOwnerId", Dog::class.java)

        assertThat(ownerLookup.parts.map { it.property.segment }).containsExactly("ownerId")
    }

    private fun withMetadata(block: (org.hibernate.boot.Metadata) -> Unit) {
        val registry = StandardServiceRegistryBuilder()
            .applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
            .build()
        try {
            block(
                MetadataSources(registry)
                    .addAnnotatedClass(Dog::class.java)
                    .addAnnotatedClass(DogPhoto::class.java)
                    .buildMetadata(),
            )
        } finally {
            StandardServiceRegistryBuilder.destroy(registry)
        }
    }
}
