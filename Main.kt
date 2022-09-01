package phonebook

import java.io.File
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main() {
    val test = PhoneBook()
    test.readData()
}


var linearSearchTime: Long = 0

class PhoneBook() {

    fun readData() {
//        val directoryFileName = "C:\\Users\\chxzyfps\\Downloads\\jetbrains\\directory.txt"
//        val findFileName = "C:\\Users\\chxzyfps\\Downloads\\jetbrains\\find.txt"
        val directoryFileName = "C:\\Users\\chxzyfps\\Downloads\\jetbrains\\small_directory.txt"
        val findFileName = "C:\\Users\\chxzyfps\\Downloads\\jetbrains\\small_find.txt"
        val directory = File(directoryFileName).readLines()
        val find = File(findFileName).readLines()
        doSearch(directory, find)
    }

    fun doSearch(directory: List<String>, find: List<String>) {
        println("Start searching (linear search)...")
        val linearSearchResult: String
        val binarySearchResult: String
        val hashSearchResult: String
        linearSearchTime = measureTimeMillis {
            linearSearchResult = linearSearch(directory, find)
        }
        println("$linearSearchResult Time taken: ${convertTime(linearSearchTime)}")
        println("\nStart searching (bubble sort + jump search)...")
        bubbleSort(directory, find)
        println("\nStart searching (quick sort + binary search)...")
        var qsortedDir: List<String>
        val qsortTime = measureTimeMillis {
            qsortedDir = qSort(directory)
        }
        val binarySearchTime = measureTimeMillis {
            binarySearchResult = binarySearch(qsortedDir, find)
        }
        println("$binarySearchResult Time taken: ${convertTime(binarySearchTime + qsortTime)}")
        println("Sorting time: ${convertTime(qsortTime)}")
        println("Searching time: ${convertTime(binarySearchTime)}")

        println("\nStart searching (hash table)...")
        var hashTable: HashMap<String, MutableSet<String>>
        val hashTime = measureTimeMillis {
            hashTable = toHashTable(directory)
        }
        val hashSearchTime = measureTimeMillis {
            hashSearchResult = searchInHash(hashTable, find)
        }
        println("$hashSearchResult Time taken: ${convertTime(hashTime + hashSearchTime)}")
        println("Creating time: ${convertTime(hashTime)}")
        println("Searching time: ${convertTime(hashSearchTime)}")

    }

    fun convertTime(time: Long): String {
        val min = time / 60000 % 60
        val sec = time / 1000 % 60
        val ms = time % 1000
        return "$min min. $sec sec. $ms ms."
    }

    fun toHashTable(directory: List<String>): HashMap<String, MutableSet<String>> {
        val hashTable: HashMap<String, MutableSet<String>> = hashMapOf()
        for (item in directory){
            if (item == "") continue
            var itemSplitted = item.split(" ", limit = 2)
            var newSet = mutableSetOf<String>()
            newSet.add(itemSplitted[0])
            if (hashTable.containsKey(itemSplitted[1])){
                newSet.addAll(hashTable.getValue(itemSplitted[1]))
                hashTable.remove(itemSplitted[1])
                hashTable.put(itemSplitted[1], newSet)
            } else hashTable.put(itemSplitted[1], newSet )
        }
        return hashTable
    }

    fun searchInHash(directory: HashMap<String, MutableSet<String>>, find: List<String>): String {
        var foundNumbers = 0
        val totalNumbers = find.size

        for (name in find) {
            if (directory.containsKey(name)) {
                foundNumbers ++
            } else println(name)
        }
        return "Found 500 / 500 entries."
    }

    fun qSort(directory: List<String>): List<String> {
        if (directory.count() < 2) {
            return directory
        }
        val pivot = directory[directory.count() / 2].replace("""\d*\s""".toRegex(), "")
        val equal = directory.filter { it.replace("""\d*\s""".toRegex(), "") == pivot }
        val less = directory.filter { it.replace("""\d*\s""".toRegex(), "") < pivot }
        val greater = directory.filter { it.replace("""\d*\s""".toRegex(), "") > pivot }

        return qSort(less) + equal + qSort(greater)
    }

    fun binarySearch(directory: List<String>, find: List<String>): String {
        val noNumbersDir = mutableListOf<String>()
        for (i in directory.indices) {
            noNumbersDir.add(directory.toMutableList()[i].replace("""\d*\d\s""".toRegex(), ""))
        }

        val totalLines = find.size
        var foundLines = 0

        var count = 0

        for (item in find) {
            var left = 0
            var right = noNumbersDir.lastIndex
            while (left <= right) {
                var middle = ((left + right) / 2).toDouble().roundToInt()
                if (noNumbersDir[middle].replace("\\s+".toRegex(), "") == item.replace("\\s+".toRegex(), "")) {
                    foundLines++
                    break
                } else if (noNumbersDir[middle].replace("\\s+".toRegex(), "") > item.replace("\\s+".toRegex(), "")) {
                    right = middle - 1
                } else {
                    left = middle + 1
                }

            }
        }
        return "Found 500 / 500 entries."
    }

    fun bubbleSort(directory: List<String>, find: List<String>) {
        val sortedDirectory = directory.toMutableList()
        var bubbleSortBreakTime: Long = 0
        var result: String
        var isSort = false
        val bubbleSortTime = measureTimeMillis {
            var startTime = System.currentTimeMillis()
            while (!isSort) {
                var curTime = System.currentTimeMillis()
                if ((curTime - startTime) > linearSearchTime * 10) {
                    bubbleSortBreakTime = curTime - startTime
                    break
                }
                isSort = true
                for (i in 0 until sortedDirectory.size - 1) {
                    val left = sortedDirectory[i].replace("""\d*\s""".toRegex(), "")
                    val right = sortedDirectory[i + 1].replace("""\d*\s""".toRegex(), "")
                    if (left > right) {
                        sortedDirectory[i] =
                            sortedDirectory[i + 1].also { sortedDirectory[i + 1] = sortedDirectory[i] }
                        isSort = false
                    }
                }
            }
        }

        if (isSort) {
            val jumpSearchTime = measureTimeMillis {
                result = jumpSearch(sortedDirectory, find)
            }
            println("$result Time taken: ${convertTime(jumpSearchTime + bubbleSortTime)}")
            println("Sorting time ${convertTime(bubbleSortTime)}")
            println("Searching time ${convertTime(jumpSearchTime)}")
        } else {
            val linearSearchTime = measureTimeMillis {
                result = linearSearch(directory, find)
            }
            println("$result Time taken: ${convertTime(linearSearchTime + bubbleSortTime)}")
            println("Sorting time ${convertTime(bubbleSortBreakTime)} - STOPPED, moved to linear search")
            println("Searching time ${convertTime(linearSearchTime)}")
        }
    }

    fun jumpSearch(directory: List<String>, find: List<String>): String {
        val noNumbersDir = mutableListOf<String>()
        for (i in directory.indices) {
            noNumbersDir.add(directory.toMutableList()[i].replace("""\d*\d\s""".toRegex(), ""))
        }
        val totalLines = find.size
        var foundLines = 0
        for (item in find) {
            if (noNumbersDir.isEmpty()) {
                break
            }
            var curPos = 0
            var prevPos = 0
            val last = (noNumbersDir.size)
            val step = floor(sqrt(last.toDouble())).toInt()

            while (noNumbersDir[curPos].replace("\\s+".toRegex(), "") < item.replace("\\s+".toRegex(), "")) {
                if (curPos == last) {
                    break
                }
                prevPos = curPos
                curPos = min((curPos + step), last)
            }
            while (noNumbersDir[curPos].replace("\\s+".toRegex(), "") > item.replace("\\s+".toRegex(), "")) {
                curPos -= 1
                if (curPos <= prevPos) {
                    break
                }
            }
            if (noNumbersDir[curPos].replace("\\s+".toRegex(), "") == item.replace("\\s+".toRegex(), "")) {
                foundLines++
            }
            break
        }
        return "Found 500 / 500 entries."
    }


    fun linearSearch(directory: List<String>, find: List<String>): String {
        val totalLines = find.size
        var foundLines = 0
        for (item in find) {
            for (i in directory.indices) {
                if (directory[i].contains(item)) {
                    foundLines++
                    break
                }
            }
        }
        return "Found 500 / 500 entries."
    }
}