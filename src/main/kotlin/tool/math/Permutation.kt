package tool.math


// return all combinations of split in (split, splitSize)
//   (   split   )
//   ( splitSize )
//
fun <T> getCombinationList(source:List<T>, splitSize: Int): List<List<T>> {
    val data = source.take(splitSize).toMutableList()
    val arr = source.toMutableList()
    val allCombinationsList = mutableListOf<List<T>>()
    combinationUtil(arr, data, 0, source.size - 1, 0, splitSize, allCombinationsList)
    return allCombinationsList
}

/* This code is contributed by Devesh Agrawal (and altered by myself) */
private fun <T> combinationUtil(arr: MutableList<T>, data: MutableList<T>, start: Int, end: Int, index: Int, r: Int, allCombinationsList: MutableList<List<T>>) {
    if (index == r) {
        allCombinationsList.add(data.toList())
        return
    }

    var i = start
    while (i <= end && end - i + 1 >= r - index) {
        data[index] = arr[i]
        combinationUtil(arr, data, i + 1, end, index + 1, r, allCombinationsList)
        i++
    }
}

/**
 * returns all permutations of the given list.
 * each 'list' in the returned list of 'list's, contains one of the permutations.
 * be aware of the combinatorial explosion!
 *
 */

fun <T> makeAllPermutations(elements: List<T>): List<List<T>> {
    return makeAllPermutations(elements.size, elements.toMutableList())
}

private fun <T> swap(elements: MutableList<T>, a: Int, b: Int) {
    val tmp = elements[a]
    elements[a] = elements[b]
    elements[b] = tmp
}

private fun <T> makeAllPermutations(n: Int, elements: MutableList<T>): List<List<T>> {
    if (n == 1) {
        return listOf(elements.toList())
    } else {
        val localList = mutableListOf<List<T>>()
        for (i in 0.. n - 2) {
            localList.addAll(makeAllPermutations(n - 1, elements))
            if (n % 2 == 0) {
                swap(elements, i, n - 1)
            } else {
                swap(elements, 0, n - 1)
            }
        }
        localList.addAll(makeAllPermutations(n - 1, elements))
        return localList
    }
}

fun Long.fac():Long {
    if (this == 0L)
        return 1L
    return (1..this).reduce { acc, i ->  acc*i}
}

fun Int.fac():Long {
    return this.toLong().fac()
}


//calculate n! / (n-k)!
private fun perm(n: Int, k: Int): Int {
    var result = 1
    for (i in n downTo (n - k + 1))
        result *= i
    return result
}

//calculate an index for n!/k! serie of numbers
// - poolsize is n
// - seq is a list of k numbers xi, 0 <= xi < n

private fun permutationIndex(seq: List<Int>, poolSize: Int): Int {
    val pool = (0 until poolSize).toMutableList()
    var index = 0
    val k = seq.size

    for (i in 0 until k) {
        val pos = pool.indexOf(seq[i])
        val remaining = poolSize - i - 1
        index += pos * perm(remaining,k - i - 1)
        pool.removeAt(pos)
    }

    return index
}

//calculate an index for n! serie of numbers
private fun permutationIndex(permutation: List<Int>): Int {
    var index = 0
    var position = 2 // position 1 is paired with factor 0 and so is skipped
    var factor = 1
    for (p in permutation.size - 2 downTo 0) {
        var successors = 0
        for (q in p + 1..<permutation.size) {
            if (permutation[p] > permutation[q]) {
                successors++
            }
        }
        index += (successors * factor)
        factor *= position
        position++
    }
    return index
}

