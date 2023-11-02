package com.example.t9ahowtodie

import android.util.Log
import com.example.t9ahowtodie.ui.D6
import com.example.t9ahowtodie.ui.NORMAL
import com.example.t9ahowtodie.ui.REROLL_FAILED
import com.example.t9ahowtodie.ui.REROLL_ONES
import com.example.t9ahowtodie.ui.REROLL_SIXES
import com.example.t9ahowtodie.ui.REROLL_SUCCESS
import kotlin.math.min
import kotlin.math.pow

/* 1-dimensional argmin */
fun <T : Comparable<T>> Iterable<T>.argmin(): Int {
    return withIndex().minBy { it.value }.index
}

/* 1-dimensional argmax */
fun <T : Comparable<T>> Iterable<T>.argmax(): Int {
    return withIndex().maxBy { it.value }.index
}

/* Calculate the binomial
* TODO replace with its DP solution */
fun binomialRecursive(n: Int, k: Int): Double {
    if (k < 0 || k > n)
        return 0.0
    var realk = k
    if (k > n - k) // Take advantage of symmetry
        realk = n - k
    if (realk == 0 || n <= 1)
        return 1.0
    return binomialRecursive(n - 1, realk) + binomialRecursive(n - 1, realk - 1)
}

/* This function returns the number of ways we can obtain less than (or more than,
* according to the moreThan param) a certain diceSum on a sum of nDice dice.
* The size of the array max_exclude accounts for the number of minimized instances
* The size of the array min_exclude accounts for the number of maximized instances
* Recursive version ( O(n^6)) */
fun findWays_recursive (
    nDice: Int,
    diceSum: Int,
    max_exclude: List<Int> = listOf<Int>(),  // Defaulting to normal roll
    min_exclude: List<Int> = listOf<Int>(),  // Defaulting to normal roll
    moreThan: Boolean = false) : Double {

    if (nDice == 0) // We ran out of dice
        return if (
            (moreThan && (diceSum + max_exclude.sum() + min_exclude.sum() <= 0)) ||
            (!moreThan && (diceSum + max_exclude.sum() + min_exclude.sum() >= 0))
        )
            1.0
        else
            0.0

    var cnt : Double = 0.0
    val max_exclude_copy = max_exclude.toMutableList()
    val min_exclude_copy = min_exclude.toMutableList()

    for (i in 1 .. D6) {
        if (max_exclude.isNotEmpty() && i > max_exclude.min())
            max_exclude_copy[max_exclude.argmin()] = i
        if (min_exclude.isNotEmpty() && i < min_exclude.max())
            min_exclude_copy[min_exclude.argmax()] = i

        cnt += findWays_recursive(
            nDice = nDice - 1,
            diceSum = diceSum - i,
            max_exclude = max_exclude_copy,
            min_exclude = min_exclude_copy,
            moreThan = moreThan
        )
    }
    return cnt
}




/* Probability of rolling less (or more according to the moreThan param) than a certain threshold
* on a sum of nDice dice.
* we count in the instances of maximized or minimized, which default to 0 on a normal roll. */
fun calculateTestBaseProbability(
    nDice: Int,
    threshold: Int,
    minimized: Int = 0,
    maximized: Int = 0,
    moreThan: Boolean = false) : Double {

    return ( findWays_recursive(
        nDice = nDice + maximized + minimized,
        diceSum = threshold,
        max_exclude = MutableList(minimized) { 1 },
        min_exclude = MutableList(maximized) { D6 },
        moreThan = moreThan
    ) / D6.toDouble().pow(nDice + maximized + minimized) )
}

/* Probability that the sum of nDice Dice is equal to threshold */
@Deprecated("This function finds no use")
fun probabilitySumEqualsOnNDice(nDice: Int, threshold: Int): Double {

    Log.d("Debug", "Calling with $nDice and $threshold")

    /* Corner case */
    if (threshold < nDice || threshold > nDice * D6)
        return 0.0

    var sum: Double = 0.0
    for (i in 0..(threshold - nDice).div(D6)) {
        sum += (-1.0).pow(i) *
                binomialRecursive(nDice, i) *
                binomialRecursive(threshold - D6 * i - 1, nDice - 1)
    }

    var result = (sum / (D6.toDouble()).pow(nDice))
    Log.d("p_result", "$result" )
    return result
}

/* Probability that the sum of nDice Dice is less or equal to threshold */
@Deprecated("Use calculateTestBaseProbability instead")
fun probabilitySumLeqOnNDice(nDice: Int, threshold: Int): Double {

    Log.d("Debug", "Calling with $nDice and $threshold")

    /* Corner case */
    if (threshold < nDice || threshold > nDice * D6)
        return 0.0

    var sum: Double = 0.0
    for (i in 0 .. (threshold - nDice).div(D6)) {
        sum += (-1.0).pow(i) *
                binomialRecursive(nDice, i) *
                binomialRecursive((threshold - D6 * i), nDice)
    }
    return (sum / ((D6.toDouble()).pow(nDice)))
}

/* Probability that the sum of nDice Dice is greater or equal to threshold */
@Deprecated("Use calculateTestBaseProbability instead")
fun probabilitySumGeqOnNDice(nDice: Int, threshold: Int): Double {

    /* Corner case */
    if (threshold < nDice || threshold > nDice * D6)
        return 0.0

    return ( 1 - probabilitySumLeqOnNDice(nDice, threshold - 1) )
}

fun chancesAttack(idx : Int) : Double {
    return ((D6 - idx).toDouble())
}

fun chancesSave(idx : Int) : Double {
    return ((D6 - idx - 1).toDouble())
}

fun calculateAttackBaseProbability(chances : Double, modifier : Int = 0) : Double {
    return when (modifier) {
        NORMAL -> {
            (chances / D6)
        }
        REROLL_FAILED -> {
            ( (chances / D6) + ( (1.0 - (chances / D6)) * (chances / D6) ) )
        }
        REROLL_SUCCESS -> {
            ( (chances / D6).pow(2) )
        }
        REROLL_ONES -> {
            min( (chances / D6) + ( ( 1.0 / D6) * (chances / D6) ), 1.0 )
        }
        REROLL_SIXES -> {
            min( ( (chances - 1.0) / D6) + ( ( 1.0 / D6) * (chances / D6) ), 1.0 )
        }
        else -> {
            Log.wtf("Probability", "This should never happen!")
            0.0
        }
    }
}

fun calculateTestProbability(baseProbability: Double, modifier: Int): Double {

    return when (modifier) {
        NORMAL -> {
            ( baseProbability )
        }
        REROLL_FAILED -> {
            ( baseProbability + ( (1.0 - baseProbability) * baseProbability ) )
        }
        REROLL_SUCCESS -> {
            ( baseProbability.pow(2) )
        }
        else -> {
            Log.wtf("Probability", "This should never happen!")
            0.0
        }
    }

}