package cache.data

import dev.jombi.kordsb.common.entity.ComponentType
import dev.jombi.kordsb.core.cache.data.ChatComponentData
import dev.jombi.kordsb.core.cache.data.ComponentData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentDataTest {

    @Test
    fun `polymorphic ComponentData can be serialized`() {
        val type = ComponentType.ActionRow
        val data: ComponentData = ChatComponentData(type)
        assertEquals(
            expected = """{"_type":"dev.jombi.kordsb.core.cache.data.ChatComponentData","type":${type.value}}""",
            actual = Json.encodeToString(data),
        )
    }
}
