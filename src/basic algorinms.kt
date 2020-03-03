package phonebook

import java.io.File
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis


fun main() {
    val dataFile = "C:/Java/filesForPhoneBook/directory.txt"
    val searchingFile = "C:/Java/filesForPhoneBook/find.txt"
    val dataList = File(dataFile).readLines().toMutableList()
    val findList = File(searchingFile).readLines().toMutableList()
    var found = 0
    var tooLong = false
    //Linear search
    println("Start searching...")
    var linearSearchTime = measureTimeMillis {
        for (i in findList) {
            if (linearSearch(dataList, i)) found++
        }
    }
    println("Found $found / ${findList.size} entries. Time taken: ${printTime(linearSearchTime)}")
    println()
    println("Start searching (bubble sort + jump search)...")
    //Bubble sorting
    var startTime = System.currentTimeMillis()
    val dataListSorted = bubbleSort(dataList, startTime, linearSearchTime)
    val bubbleSortTime = System.currentTimeMillis() - startTime
    if (dataListSorted == null) { //If bubble sorting failed do linear search
        found = 0
        linearSearchTime = measureTimeMillis {
            for (i in findList) {
                if (linearSearch(dataList, i)) found++
            }
        }
        tooLong = true
        printResult(found, findList.size, bubbleSortTime, linearSearchTime, tooLong)
    } else { //If bubble sorting success do jump search
        found = 0
        val jumpSearchTime = measureTimeMillis {
            for (i in findList) {
                if (jumpSearch(dataListSorted, i)) found++
            }
        }
        printResult(found, findList.size, bubbleSortTime, jumpSearchTime, tooLong)
    }
    println("Start searching (quick sort + binary search)...")
    //Quick sorting
    startTime = System.currentTimeMillis()
    val dataListQuickSorted = quickSort(dataList, 0, dataList.size - 1)
    val quickSortTime = System.currentTimeMillis() - startTime
    var foundBinary = 0
    //Binary search
    val binarySearchTime = measureTimeMillis {
        for (i in findList) {
            if (binarySearch(dataListQuickSorted, i)) foundBinary++
        }
    }
    printResult(foundBinary, findList.size, quickSortTime, binarySearchTime, false)
    println("Start searching (hash table)...")
    startTime = System.currentTimeMillis()
    val dataMap = dataList.associateWith { it.substringAfter(" ").hashCode() }
    val transformTime = System.currentTimeMillis() - startTime
    var tableFound = 0
    val tableSearchTime = measureTimeMillis {
        for (i in findList) {
            if (dataMap.containsValue(i.hashCode())) tableFound++
        }
    }
    println("Found $tableFound / ${findList.size} entries. Time taken: ${printTime(transformTime + tableSearchTime)}")
    println("Creating time: ${printTime(transformTime)}")
    println("Searching time: ${printTime(tableSearchTime)}")
}

fun quickSort(dataList: MutableList<String>, first: Int, last: Int): List<String> {
    //Quick sorting function: rightmos pivot, recursive
    val pivot = dataList[last].substringAfter(" ")
    var left = first
    var right = last
    do {
        while (dataList[left].substringAfter(" ") < pivot ) left++
        while (dataList[right].substringAfter(" ") > pivot) right--
        if (left <= right) {
            dataList.swap(left, right)
            left++
            right--
        }
    } while (left < right)
    if (first < right) quickSort(dataList, first, right)
    if (last > left) quickSort(dataList, left, last)
    return dataList
}

fun bubbleSort(dataList: MutableList<String>, startTime: Long, linearSearchTime: Long): List<String>? {
    //Buuble sorting function
    for (i in dataList.indices) {
        for (j in 0 until (dataList.size - 1 - i)) {
            if (dataList[j].substringAfter(" ") > dataList[j + 1].substringAfter(" ")) {
                dataList.swap(j, j + 1)
            }
        }
        if (System.currentTimeMillis() - startTime > linearSearchTime * 10) return null
    }
    return dataList
}

fun MutableList<String>.swap(left: Int, right: Int) {
    //Function for swapping items
    val stack = this[left]
    this[left] = this[right]
    this[right] = stack
}

fun binarySearch(dataListSorted: List<String>, find: String): Boolean {
    //Binary search function, iterable
    var left = 0
    var right = dataListSorted.size - 1
    while (left <= right) {
        val mid = (left + right) / 2
        when {
            dataListSorted[mid].substringAfter(" ") == find -> return true
            dataListSorted[mid].substringAfter(" ") > find -> right = mid - 1
            dataListSorted[mid].substringAfter(" ") < find -> left = mid + 1
        }
    }
    return false
}

fun jumpSearch(dataListSorted: List<String>, find: String): Boolean {
    //Jump search function
    var endJump: Int
    val jump = sqrt(dataListSorted.size.toDouble()).toInt()
    if (find >= dataListSorted[0] && find <= dataListSorted.last()) {
        for (j in dataListSorted.indices step jump) {
            endJump = if (j + jump > dataListSorted.lastIndex) dataListSorted.lastIndex else j + jump
            if (find <= dataListSorted[endJump]) {
                for (k in endJump downTo j) {
                    if (dataListSorted[k].contains(find)) return true
                }
                break
            }
        }
    }
    return false
}

fun linearSearch(dataList: List<String>, find: String): Boolean {
    //Just linear search
    for (j in dataList) {
        if (j.contains(find)) return true
    }
    return false
}

fun printTime(time: Long) = "${time / 60000} min. ${((time - time % 1000) / 1000) % 60} sec. ${time % 1000} ms."
//Time from msec to min, sec, msec

fun printResult(found: Int, items: Int, sortingTime: Long, searchingTime: Long, tooLong: Boolean) {
    //Printing result
    val totalTime = sortingTime + searchingTime
    println("Found $found / $items entries. Time taken: ${printTime(totalTime)}")
    print("Sorting time: ${printTime(sortingTime)}")
    println(if (tooLong) "- STOPPED, moved to linear search" else "")
    println("Searching time: ${printTime(searchingTime)}")
    println()
}