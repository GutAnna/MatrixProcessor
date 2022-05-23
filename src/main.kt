package processor

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow

enum class ModeTranspose {
    MAIN, SIDE, VERTICAL, HORIZONTAL
}

class Matrix(val m: Int, val n: Int) {
    private val arr = mutableListOf<MutableList<Double>>()
    fun read() {
        for (i in 0 until m) {
            arr.add(
                readLine()!!.split(" ")
                    .map { it.toDouble() }.toMutableList()
            )
        }
    }

    fun print() {
        val decimalFormat = DecimalFormat("#.###")
        decimalFormat.roundingMode = RoundingMode.DOWN
        for (i in 0 until m) {
            println(arr[i].joinToString(separator = " ") { decimalFormat.format(it) })
        }
    }

    fun equalsSize(b: Matrix): Boolean {
        return b.m == m || b.n == n
    }

    fun plus(matrix: Matrix): Matrix {
        val c = Matrix(m, n)
        for (i in 0 until m) {
            val line = mutableListOf<Double>()
            for (j in 0 until n) {
                line.add(arr[i][j] + matrix.arr[i][j])
            }
            c.arr.add(line)
        }
        return c
    }

    fun multiply(matrix: Matrix): Matrix {
        val c = Matrix(m, matrix.n)
        for (i in 0 until m) {
            val line = mutableListOf<Double>()
            for (j in 0 until matrix.n) {
                var newEl = 0.0
                for (k in 0 until n) {
                    newEl += arr[i][k] * matrix.arr[k][j]
                }
                line.add(newEl)
            }
            c.arr.add(line)
        }
        return c
    }

    fun multiplyByNumber(number: Double): Matrix {
        val c = Matrix(m, n)
        for (i in 0 until m) {
            val line = mutableListOf<Double>()
            for (j in 0 until n) {
                line.add(arr[i][j] * number)
            }
            c.arr.add(line)
        }
        return c
    }

    fun transpose(param: String): Matrix? {
        val c = Matrix(m, n)
        for (i in 0 until m) {
            val line = mutableListOf<Double>()
            for (j in 0 until n) {
                when (param.toInt() - 1) {
                    ModeTranspose.MAIN.ordinal -> line.add(arr[j][i])
                    ModeTranspose.SIDE.ordinal -> line.add(arr[n - 1 - j][m - 1 - i])
                    ModeTranspose.VERTICAL.ordinal -> line.add(arr[i][n - 1 - j])
                    ModeTranspose.HORIZONTAL.ordinal -> line.add(arr[m - 1 - i][j])
                }
            }
            c.arr.add(line)
        }
        return c
    }

    private fun minor(matrix: Matrix, col: Int, row: Int = 0): Double {
        val subMatrix = Matrix(matrix.m - 1, matrix.n - 1)
        for (i in 0 until matrix.m) {
            if (i == row) continue
            val line = mutableListOf<Double>()
            for (j in 0 until matrix.n) {
                if (j != col) line.add(matrix.arr[i][j])
            }
            subMatrix.arr.add(line)
        }
        return calcDeterminant(subMatrix)
    }

    fun calcDeterminant(a: Matrix): Double {
        return if (a.m == 2) a.arr[0][0] * a.arr[1][1] - a.arr[0][1] * a.arr[1][0]
        else {
            var sum = 0.0
            for (i in 0 until a.n) {
                sum += a.arr[0][i] * minor(a, i) * (-1.0).pow(i.toDouble())
            }
            sum
        }
    }

    fun getMinorMatrix(): Matrix {
        val c = Matrix(m, n)
        for(i in 0 until m) {
            val line = mutableListOf<Double>()
            for (j in 0 until n) {
                line.add(minor(this,j,i) * (-1.0).pow((i+j).toDouble()))
            }
            c.arr.add(line)
        }
        return c
    }
}

fun oneMatrixRead(label: String = ""): Matrix {
    println("Enter size of ${label}matrix:")
    val (m1, n1) = readLine()!!.split(" ").map { it.toInt() }
    val a = Matrix(m1, n1)
    println("Enter ${label}matrix:")
    a.read()
    return a
}

fun main() {
    while (true) {
        println(
            "1. Add matrices\n" +
                    "2. Multiply matrix by a constant\n" +
                    "3. Multiply matrices\n" +
                    "4. Transpose matrix\n" +
                    "5. Calculate a determinant\n" +
                    "6. Inverse matrix\n" +
                    "0. Exit"
        )
        println("Your choice:")
        var result: Any? = null
        when (readLine()!!) {
            "1" -> {
                val a = oneMatrixRead("first ")
                val b = oneMatrixRead("second ")
                if (a.equalsSize(b)) result = a.plus(b)
            }
            "2" -> {
                val a = oneMatrixRead()
                println("Enter constant:")
                result = a.multiplyByNumber(readLine()!!.toDouble())
            }
            "3" -> {
                val a = oneMatrixRead("first ")
                val b = oneMatrixRead("second ")
                if (a.n == b.m) result = a.multiply(b)
            }
            "4" -> {
                println(
                    "1. Main diagonal\n" +
                            "2. Side diagonal\n" +
                            "3. Vertical line\n" +
                            "4. Horizontal line"
                )
                println("Your choice:")
                val param = readLine()!!
                val a = oneMatrixRead()
                result = a.transpose(param)
            }
            "5" -> {
                val a = oneMatrixRead()
                result = a.calcDeterminant(a)
            }
            "6" -> {
                val a = oneMatrixRead()
                val det = a.calcDeterminant(a)
                if (det == 0.0) {
                    println("This matrix doesn't have an inverse.")
                } else {
                    result = (a.getMinorMatrix().transpose("1"))?.multiplyByNumber(1 / det)
                }
            }
            "0" -> return
        }
        println("The result is:")
        when (result) {
            null -> println("The operation cannot be performed.")
            is Matrix -> result.print()
            else -> println(result)
        }
    }
}

