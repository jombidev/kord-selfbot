package dev.jombi.kordsb.rest.builder

import dev.jombi.kordsb.common.entity.Overwrite
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.rest.builder.channel.CategoryModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CategoryModifyBuilderTest {

    @Test
    fun `builder does not create empty overwrites by default`() {
        val builder = CategoryModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Iterable<Overwrite>>(), request.permissionOverwrites)
    }

}