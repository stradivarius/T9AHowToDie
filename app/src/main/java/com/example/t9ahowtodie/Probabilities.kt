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

/* Probability that the sum of nDice Dice is equal to threshold */
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