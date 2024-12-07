object Regexs {
    val NUMBERS = Regex("""-?\d+""")
}

fun String.extractNumbers(): List<Int> {
    return Regexs.NUMBERS.findAll(this).map { it.value.toInt() }.toList()
}

fun String.extractLongNumbers(): List<Long> {
    return Regexs.NUMBERS.findAll(this).map { it.value.toLong() }.toList()
}