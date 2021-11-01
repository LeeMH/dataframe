package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.length
import org.jetbrains.kotlinx.dataframe.api.lowercase
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.movingAverage
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.nullToZero
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.shuffled
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortWith
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.toLeft
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.api.toTop
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.jetbrains.kotlinx.dataframe.api.withValue
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.pathOf
import org.junit.Test

class Modify : TestBase() {

    @Test
    fun update() {
        // SampleStart
        df.update { age }.with { it * 2 }
        df.update { dfsOf<String>() }.with { it.uppercase() }
        df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
        df.update { weight }.at(1..4).notNull { it / 2 }
        df.update { name.lastName and age }.at(1, 3, 4).withNull()
        df.update { age }.with { movingAverage(2) { age }.toInt() }
        // SampleEnd
    }

    @Test
    fun convert() {
        // SampleStart
        df.convert { age }.with { it.toDouble() }
        df.convert { dfsOf<String>() }.with { it.toCharArray().toList() }
        // SampleEnd
    }

    @Test
    fun convertTo() {
        // SampleStart
        df.convert { age }.to<Double>()
        df.convert { numberCols() }.to<String>()
        df.convert { name.firstName and name.lastName }.to { it.length() }
        df.convert { weight }.toFloat()
        // SampleEnd
    }

    @Test
    fun replace() {
        // SampleStart
        df.replace { name }.with { name.firstName }
        df.replace { stringCols() }.with { it.lowercase() }
        df.replace { age }.with { 2021 - age named "year" }
        // SampleEnd
    }

    @Test
    fun shuffled() {
        // SampleStart
        df.shuffled()
        // SampleEnd
    }

    @Test
    fun fillNulls() {
        // SampleStart
        df.fillNulls { intCols() }.with { -1 }
        // same as
        df.update { intCols() }.where { it == null }.with { -1 }
        // SampleEnd
    }

    @Test
    fun nullToZero() {
        // SampleStart
        df.nullToZero { weight }
        // SampleEnd
    }

    @Test
    fun move() {
        // SampleStart
        df.move { age }.toLeft()

        df.move { weight }.to(1)

        // name -> info.name
        df.move { name }.into("info", "name")

        // name -> info.name
        df.move { age }.into { pathOf("info", it.name) }

        // firstName -> fullName.firstName
        // lastName -> fullName.lastName
        df.move { age and weight }.under("info")

        // name.firstName -> fullName.first
        // name.lastName -> fullName.last
        df.move { name.firstName and name.lastName }.into { pathOf("fullName", it.name.dropLast(4)) }

        dataFrameOf("a.b.c", "a.d.e")(1, 2)
            .move { all() }.into { it.name.split(".").toPath() }

        // name.firstName -> firstName
        // name.lastName -> lastName
        df.move { name.cols() }.toTop()

        // group1.default.name -> defaultData
        // group2.field.name -> fieldData
        df.move { dfs { it.name == "data" } }.toTop { it.parent!!.name + "Data" }
        // SampleEnd
    }
    
    @Test
    fun sortBy_properties() {
        // SampleStart
        df.sortBy { age }
        df.sortBy { age and name.firstName.desc }
        df.sortBy { weight.nullsLast }
        // SampleEnd
    }

    @Test
    fun sortBy_accessors() {
        // SampleStart
        val age by column<Int>()
        val weight by column<Int?>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.sortBy { age }
        df.sortBy { age and firstName }
        df.sortBy { weight.nullsLast }
        // SampleEnd
    }

    @Test
    fun sortBy_strings() {
        // SampleStart
        df.sortBy("age")
        df.sortBy { "age" and "name"["firstName"].desc }
        df.sortBy { "weight".nullsLast }
        // SampleEnd
    }
    
    @Test
    fun sortByDesc_properties() {
        // SampleStart
        df.sortByDesc { age and weight }
        // SampleEnd
    }

    @Test
    fun sortByDesc_accessors() {
        // SampleStart
        val age by column<Int>()
        val weight by column<Int?>()

        df.sortByDesc { age and weight }
        // SampleEnd
    }

    @Test
    fun sortByDesc_strings() {
        // SampleStart
        df.sortByDesc("age", "weight")
        // SampleEnd
    }

    @Test
    fun sortWith() {
        // SampleStart
        df.sortWith { row1, row2 ->
            when {
                row1.age < row2.age -> -1
                row1.age > row2.age -> 1
                else -> row1.name.firstName.compareTo(row2.name.firstName)
            }
        }
        // SampleEnd
    }
}
