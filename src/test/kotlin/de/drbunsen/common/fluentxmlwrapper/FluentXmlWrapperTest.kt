package de.drbunsen.common.fluentxmlwrapper

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FluentXmlWrapperTest {
    @Test
    fun testSimple() {
        assertEquals("some text", FluentXmlWrapper.of("<example><subnode>some text</subnode></example>")
            .getElement("subnode")
            .getText())
    }
}