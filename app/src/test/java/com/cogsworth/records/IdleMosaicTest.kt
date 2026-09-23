package com.cogsworth.records

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdleMosaicTest {
    @Test
    fun `does not repeat covers when enough unique images are available`() {
        val sources = (1..70).map { "cover-$it" } + listOf("cover-1", "cover-2")

        val selected = selectMosaicCovers(sources)

        assertEquals(60, selected.size)
        assertEquals(60, selected.distinct().size)
    }

    @Test
    fun `distributes repeats as evenly as possible when images are limited`() {
        val selected = selectMosaicCovers(listOf("a", "a", "b", "c"), targetSize = 8)
        val counts = selected.groupingBy { it }.eachCount()

        assertEquals(setOf("a", "b", "c"), counts.keys)
        assertEquals(8, selected.size)
        assertTrue(counts.values.max() - counts.values.min() <= 1)
    }

    @Test
    fun `returns no covers for an empty source`() {
        assertTrue(selectMosaicCovers(emptyList()).isEmpty())
    }
}
