package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test

class AddTests {

    @Test
    fun `add with new`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        val added = df.add("Y") { if (x() == 0) 0 else (prev()?.newValue() ?: 0) + 1 }
        val expected = listOf(1, 2, 0, 1, 2, 3, 4, 0, 1, 2)
        added["Y"].values() shouldBe expected
    }

    @Test
    fun `throw for newValue at the next row`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        shouldThrow<IndexOutOfBoundsException> {
            df.add("y") { next()?.newValue() ?: 1 }
        }
    }
}
