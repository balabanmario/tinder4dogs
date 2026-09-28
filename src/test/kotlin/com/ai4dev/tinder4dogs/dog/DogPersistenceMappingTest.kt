package com.ai4dev.tinder4dogs.dog

import jakarta.persistence.Column
import jakarta.persistence.Table
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.AvailableSettings
import org.junit.jupiter.api.Test

class DogPersistenceMappingTest {

    @Test
    fun `dog profile persistence mapping matches migration columns`() {
        val migrationColumns = listOf(
            "/db/changelog/changes/001-create-dog.sql",
            "/db/changelog/changes/005-create-dog-profile.sql",
        ).flatMap { path ->
            requireNotNull(javaClass.getResource(path)).readText().lineSequence().toList()
        }.flatMap { line ->
            Regex("(?:ADD COLUMN\\s+|^\\s*)([a-z_]+)\\s+(?:VARCHAR|TEXT|UUID|INT|BOOLEAN|BIGINT|TIMESTAMPTZ)").findAll(line)
                .map { it.groupValues[1] }
                .toList()
        }.toSet()

        val registry = StandardServiceRegistryBuilder()
            .applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
            .build()
        try {
            val metadata = MetadataSources(registry)
                .addAnnotatedClass(Dog::class.java)
                .buildMetadata()
            val dogMapping = metadata.getEntityBinding(Dog::class.java.name)
            val mappedColumnsByProperty = dogMapping.propertyClosure.associate { property ->
                property.name to property.value.selectables.map { selectable -> selectable.text }
            }
            val expectedColumns = mapOf(
                "name" to "name",
                "breed" to "breed",
                "age" to "age",
                "ownerId" to "owner_id",
                "sex" to "sex",
                "size" to "size",
                "energyLevel" to "energy_level",
                "temperament" to "temperament",
                "matingMode" to "mating_mode",
                "pedigree" to "pedigree",
                "neuteringStatus" to "neutering_status",
                "healthStatus" to "health_status",
                "profileComplete" to "profile_complete",
                "missingFields" to "missing_fields",
            )

            assertThat(dogMapping.table.name).isEqualTo(Dog::class.java.getAnnotation(Table::class.java).name)
            expectedColumns.forEach { (property, column) ->
                assertThat(mappedColumnsByProperty[property]).containsExactly(column)
                assertThat(migrationColumns).contains(column)
            }
            assertThat(mappedColumnsByProperty).doesNotContainKey("gender")

            val fields = setOf("name", "ownerId", "sex", "matingMode", "profileComplete", "missingFields")
            fields.forEach { fieldName ->
                assertThat(Dog::class.java.getDeclaredField(fieldName).getAnnotation(Column::class.java))
                    .isNotNull
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry)
        }
    }
}
