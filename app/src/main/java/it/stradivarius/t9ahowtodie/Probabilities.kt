package it.stradivarius.t9ahowtodie

import android.util.Log
import it.stradivarius.t9ahowtodie.ui.D6
import it.stradivarius.t9ahowtodie.ui.NORMAL
import it.stradivarius.t9ahowtodie.ui.REROLL_FAILED
import it.stradivarius.t9ahowtodie.ui.REROLL_ONES
import it.stradivarius.t9ahowtodie.ui.REROLL_SIXES
import it.stradivarius.t9ahowtodie.ui.REROLL_SUCCESS
import kotlin.math.max
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

    for (i in 1 .. D6) {
        val max_exclude_copy = max_exclude.toMutableList()
        val min_exclude_copy = min_exclude.toMutableList()

        if (max_exclude.isNotEmpty() && i > max_exclude.min()) // Minimized
            max_exclude_copy[max_exclude.argmin()] = i
        if (min_exclude.isNotEmpty() && i < min_exclude.max()) // Maximized
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

/* What are the chances of success in an attack based on the id of the selection */
fun chancesAttack(idx : Int) : Double {
    return ((D6 - idx).toDouble())
}

/* What are the chances of success in a save based on the id of the selection */
fun chancesSave(idx : Int) : Double {
    return ((D6 - idx - 1).toDouble())
}

/* Calculate the base chances of an attack to be successful */
@Deprecated("use calculate calculateAttackBaseProbabilitySixes")
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
            max(min( (chances / D6) + ( ( 1.0 / D6) * (chances / D6) ), 1.0 ), 0.0)
        }
        REROLL_SIXES -> {
            max(min( ( (chances - 1.0) / D6) + ( ( 1.0 / D6) * (chances / D6) ), 1.0 ), 0.0)
        }
        else -> {
            Log.wtf("Probability", "This should never happen!")
            0.0
        }
    }
}

/* Calculate the chances of an attack to be successful:
* This function returns a couple:
*   - the successful chances of obtaining a SIX
*   - the successful chances of success excluding SIXes
* The successful number of chances are the sum of the two members of the couple. */
fun calculateAttackBaseProbabilitySixes(chances : Double, modifier : Int = NORMAL) : Chance {

    var chanceSixes : Double = 0.0
    var chanceTot : Double = 1.0

    // if it is automatic, then we just return 1 with no chance of obtaining sixes
    if (chances.toInt() != D6)
        when (modifier) {
            NORMAL -> {
                chanceSixes = 1.0 / D6
                chanceTot = (chances / D6)
            }
            REROLL_FAILED -> {
                chanceSixes = ( (1.0 / D6) + ( (1.0 - (chances / D6)) * (1.0 / D6) ) )
                chanceTot = ( (chances / D6) + ( (1.0 - (chances / D6)) * (chances / D6) ) )
            }
            REROLL_SUCCESS -> {
                chanceSixes = ( (chances / D6) * (1.0 / D6) )
                chanceTot = ( (chances / D6).pow(2) )
            }
            REROLL_ONES -> {
                chanceSixes = (1.0 / D6) + ( ( 1.0 / D6).pow(2) )
                chanceTot = max(min(
                (chances / D6) + ( ( 1.0 / D6) * (chances / D6) ),
                1.0 ), 0.0)
            }
            REROLL_SIXES -> {
                chanceSixes = ( (1.0 / D6).pow(2) )
                chanceTot = max(min(
                ( (chances - 1.0) / D6) + ( ( 1.0 / D6) * (chances / D6) ),
                1.0 ), 0.0)
            }
            else -> {
                Log.wtf("Probability", "This should never happen!")
            }
        }
    return Chance(chanceTot - chanceSixes, chanceSixes)
}

/* Calculate the number of average inflicted wounds based on the number of attacks */
fun calculateAverageInflictedWounds(
    numberAttacks : Int,
    rollToHit : Int,
    rollToWound : Int,
    rollArmourSave : Int,
    rollSpecialSave : Int,
    modifierToHit : Int = NORMAL,
    modifierToWound : Int = NORMAL,
    modifierArmourSave : Int = NORMAL,
    modifierSpecialSave : Int = NORMAL,
    poisonAttacks : Boolean = false,
    lethalStrike : Boolean = false,
    fortitude : Boolean = false,
    battleFocus : Boolean = false
) : Double {
    /* Convert rolls to chances */
    val chancesToHit : Double = chancesAttack(rollToHit)
    val chancesToWound : Double = chancesAttack(rollToWound)
    val chancesArmourSave : Double = chancesSave(rollArmourSave)
    val chancesSpecialSave : Double = chancesSave(rollSpecialSave)

    /* Hit
    * split between hitNormal and HitPoison */
    val hitChance = calculateAttackBaseProbabilitySixes(chancesToHit, modifierToHit)
    var hitNormal = 0.0
    var hitPoison = 0.0
    if ( poisonAttacks ) {
        hitNormal += numberAttacks *
                hitChance.nosixes
        hitPoison += numberAttacks *
                ( if (battleFocus) (2 * hitChance.sixes) else hitChance.sixes )
    } else {
        hitNormal += numberAttacks *
                (hitChance.nosixes + ( if (battleFocus) (2 * hitChance.sixes) else hitChance.sixes))
    }

    /* Wound
    * split between woundNormal and woundLethal */
    val woundChance = calculateAttackBaseProbabilitySixes(chancesToWound, modifierToWound)
    var woundNormal = 0.0
    var woundLethal = 0.0
    if ( lethalStrike ) {
        woundNormal += hitNormal *
                woundChance.nosixes
        woundLethal += hitNormal *
                woundChance.sixes
    } else {
        woundNormal += hitNormal *
                (woundChance.nosixes + woundChance.sixes)
    }

    /* Armour save
    *  output is throughArmor and woundLethal */
    val _armourChance = calculateAttackBaseProbabilitySixes(chancesArmourSave, modifierArmourSave)
    val armourFailChance = 1.0 - ( _armourChance.sixes + _armourChance.nosixes )
    val throughArmor = (woundNormal * armourFailChance) + (hitPoison * armourFailChance)

    /* Special save
    * output is finalWounds */
    val _specialChance = calculateAttackBaseProbabilitySixes(chancesSpecialSave, modifierSpecialSave)
    val specialFailChance = 1.0 - ( _specialChance.sixes + _specialChance.nosixes )
    val finalWounds = if ( fortitude ) woundLethal + ( throughArmor * specialFailChance )
        else ( ( throughArmor + woundLethal ) * specialFailChance )

    return finalWounds
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

data class Chance(val nosixes: Double, val sixes: Double)