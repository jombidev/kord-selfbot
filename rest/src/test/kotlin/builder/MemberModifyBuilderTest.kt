package dev.jombi.kordsb.rest.builder

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.rest.builder.member.MemberModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MemberModifyBuilderTest {

    @Test
    fun `builder does not create empty roles by default`() {
        val builder = MemberModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Iterable<Snowflake>>(), request.roles)
    }

}