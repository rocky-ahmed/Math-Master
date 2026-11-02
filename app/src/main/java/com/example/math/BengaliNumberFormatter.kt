package com.example.math

object BengaliNumberFormatter {
    private val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBn(number: Int): String {
        return toBn(number.toString())
    }

    fun toBn(number: Long): String {
        return toBn(number.toString())
    }

    fun toBn(numberStr: String): String {
        val sb = StringBuilder()
        for (ch in numberStr) {
            if (ch in '0'..'9') {
                sb.append(bnDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toEn(numberStr: String): String {
        val sb = StringBuilder()
        for (ch in numberStr) {
            when (ch) {
                '০' -> sb.append('0')
                '১' -> sb.append('1')
                '২' -> sb.append('2')
                '৩' -> sb.append('3')
                '৪' -> sb.append('4')
                '৫' -> sb.append('5')
                '৬' -> sb.append('6')
                '৭' -> sb.append('7')
                '৮' -> sb.append('8')
                '৯' -> sb.append('9')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }
}