/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsvks.monaco.option

@JvmInline
value class WordWrap private constructor(override val value: String) : Option<String> {
    companion object {
        fun fromValue(value: String) = when (value) {
            "on" -> On
            "off" -> Off
            "wordWrapColumn" -> WordWrapColumn
            "bounded" -> Bounded
            else -> throw IllegalArgumentException("Unknown value: $value")
        }

        val On = WordWrap("on")
        val Off = WordWrap("off")
        val WordWrapColumn = WordWrap("wordWrapColumn")
        val Bounded = WordWrap("bounded")
    }
}

@JvmInline
value class WrappingStrategy private constructor(override val value: String) : Option<String> {
    companion object {
        fun fromValue(value: String) = when (value) {
            "simple" -> Simple
            "advanced" -> Advanced
            else -> throw IllegalArgumentException("Unknown value: $value")
        }

        val Simple = WrappingStrategy("simple")
        val Advanced = WrappingStrategy("advanced")
    }
}

@JvmInline
value class WordBreak private constructor(override val value: String) : Option<String> {
    companion object {
        fun fromValue(value: String) = when (value) {
            "normal" -> Normal
            "keepAll" -> KeepAll
            else -> throw IllegalArgumentException("Unknown value: $value")
        }

        val Normal = WordBreak("normal")
        val KeepAll = WordBreak("keepAll")
    }
}
