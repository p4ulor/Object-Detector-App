package p4ulor.obj.detector.data.utils

fun String.capitalized() = replaceFirstChar { it.uppercase() }

fun <T> List<T>.getOrRandom(index: Int) = getOrNull(index) ?: shuffled()[0]
