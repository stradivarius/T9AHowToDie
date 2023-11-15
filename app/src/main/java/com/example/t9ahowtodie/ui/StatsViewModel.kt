package com.example.t9ahowtodie.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.t9ahowtodie.calculateAttackBaseProbability
import com.example.t9ahowtodie.calculateAverageInflictedWounds
import com.example.t9ahowtodie.calculateTestBaseProbability
import com.example.t9ahowtodie.calculateTestProbability
import com.example.t9ahowtodie.chancesAttack
import com.example.t9ahowtodie.chancesSave
import com.example.t9ahowtodie.probabilitySumEqualsOnNDice
import com.example.t9ahowtodie.probabilitySumGeqOnNDice
import com.example.t9ahowtodie.probabilitySumLeqOnNDice

// whether to calculate also when the sum of N dice equals precisely a number
const val TEST_SUM_EQUALS = false

const val D6 = 6
const val MIN_MAX_INSTANCES = 3

const val NORMAL = 0
const val REROLL_FAILED = 1
const val REROLL_SUCCESS = 2
const val REROLL_ONES = 3
const val REROLL_SIXES = 4

const val POISON = 0
const val LETHAL = 1
const val BATTLE_FOCUS = 2
const val FORTITUDE = 3

/* Just a list of strings corresponding to the input value */
fun generateMaxMinInstances(instances: Int): List<String> {
    return List(instances) { it.toString() }
}

val AttackStatModifiers = arrayListOf<String>(
    "Normal",
    "Reroll Failed",
    "Reroll Success",
    "Reroll Ones",
    "Reroll Sixes"
)
val TestStatModifiers = arrayListOf<String>(
    "Normal",
    "Reroll Failed",
    "Reroll Success"
)
val MinMaxModifier = ArrayList(generateMaxMinInstances(MIN_MAX_INSTANCES))



class StatsViewModel: ViewModel() {
    val attackStatsState = mutableStateOf( AttackStatsState() )
    val testStatsState = mutableStateOf( TestStatsState() )

    /* How many different values can I obtain by summing the result of diceNumber dice? */
    fun numberOfValuesWithDice(diceNumber: Int): List<String> {
        return Array(D6 * diceNumber - diceNumber + 1){it + diceNumber}.map { it.toString() }
    }

    /* Combined probability of one attack going through alle the steps */
    @Deprecated("Old style!")
    private fun calculateAttackProbability(): Double {
        return (
                calculateAttackBaseProbability(
                    chancesAttack(attackStatsState.value.toHitIdx),
                    modifier = attackStatsState.value.toHitModifier
                ) *
                calculateAttackBaseProbability(
                    chancesAttack(attackStatsState.value.toWoundIdx),
                    modifier = attackStatsState.value.toWoundModifier
                ) *
                (1 - calculateAttackBaseProbability(
                    chancesSave(attackStatsState.value.armourSaveIdx),
                    modifier = attackStatsState.value.armourSaveModifier
                )) *
                (1 - calculateAttackBaseProbability(
                    chancesSave(attackStatsState.value.specialSaveIdx),
                    modifier = attackStatsState.value.specialSaveModifier
                ))
                )
    }

    /* Invoked anytime something changes in the attack screen and it has to be recalculated */
    fun onAttack(event: AttackStatsStateEvents?) {
        when (event) {
            is AttackStatsStateEvents.attacksNumberEntered -> {
                attackStatsState.value =
                    attackStatsState.value.copy(attacksNumber = event.attacksNumber)
            }
            is AttackStatsStateEvents.toHitChoice -> {
                attackStatsState.value =
                    attackStatsState.value.copy(toHitIdx = event.diceValue)
            }
            is AttackStatsStateEvents.toWoundChoice -> {
                attackStatsState.value =
                    attackStatsState.value.copy(toWoundIdx = event.diceValue)
            }
            is AttackStatsStateEvents.armourSaveChoice -> {
                attackStatsState.value =
                    attackStatsState.value.copy(armourSaveIdx = event.diceValue)
            }
            is AttackStatsStateEvents.specialSaveChoice -> {
                attackStatsState.value =
                    attackStatsState.value.copy(specialSaveIdx = event.diceValue)
            }
            is AttackStatsStateEvents.toHitModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(toHitModifier =
                    (attackStatsState.value.toHitModifier + 1) % AttackStatModifiers.size )
            }
            is AttackStatsStateEvents.toWoundModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(toWoundModifier =
                    (attackStatsState.value.toWoundModifier + 1) % AttackStatModifiers.size )
            }
            is AttackStatsStateEvents.armourSaveModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(armourSaveModifier =
                    (attackStatsState.value.armourSaveModifier + 1) % AttackStatModifiers.size )
            }
            is AttackStatsStateEvents.specialSaveModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(specialSaveModifier =
                    (attackStatsState.value.specialSaveModifier + 1) % AttackStatModifiers.size )
            }
            is AttackStatsStateEvents.specialAttack -> {
                when ( event.specialAttack ) {
                    POISON ->
                        attackStatsState.value = attackStatsState.value.copy (
                            poisonAttacks = !attackStatsState.value.poisonAttacks )
                    LETHAL ->
                        attackStatsState.value = attackStatsState.value.copy (
                            lethalStrike = !attackStatsState.value.lethalStrike )
                    BATTLE_FOCUS ->
                        attackStatsState.value = attackStatsState.value.copy (
                            battleFocus = !attackStatsState.value.battleFocus )
                    FORTITUDE ->
                        attackStatsState.value = attackStatsState.value.copy (
                            fortitude = !attackStatsState.value.fortitude )
                }
            }
            else -> {
                Log.v("onTest", "Event is null, screen has started.")
            }
        }

        attackStatsState.value = attackStatsState.value.copy(
            averageAttacks = calculateAverageInflictedWounds(
                numberAttacks = attackStatsState.value.attacksNumber,
                rollToHit = attackStatsState.value.toHitIdx,
                rollToWound = attackStatsState.value.toWoundIdx,
                rollArmourSave = attackStatsState.value.armourSaveIdx,
                rollSpecialSave = attackStatsState.value.specialSaveIdx,
                modifierToHit = attackStatsState.value.toHitModifier,
                modifierToWound = attackStatsState.value.toWoundModifier,
                modifierArmourSave = attackStatsState.value.armourSaveModifier,
                modifierSpecialSave = attackStatsState.value.specialSaveModifier,
                poisonAttacks = attackStatsState.value.poisonAttacks,
                lethalStrike = attackStatsState.value.lethalStrike,
                battleFocus = attackStatsState.value.battleFocus,
                fortitude = attackStatsState.value.fortitude
            )
        )
        attackStatsState.value.probability = // Probability for one attack to go through
            attackStatsState.value.averageAttacks / attackStatsState.value.attacksNumber
//        attackStatsState.value.averageAttacks =
//            attackStatsState.value.probability * attackStatsState.value.attacksNumber
    }


    /* Invoked anytime something changes in the test screen and it has to be recalculated */
    fun onTest (event: TestStatsStateEvents?) {
        when (event) {
            is TestStatsStateEvents.diceNumberEntered -> {
                testStatsState.value = testStatsState.value.copy(diceNumber = event.diceNumber)
            }
            is TestStatsStateEvents.thresholdChoice -> {
                testStatsState.value = testStatsState.value.copy(thresholdIdx = event.threshold)
            }
            is TestStatsStateEvents.testModify -> {
                testStatsState.value = testStatsState.value.copy(
                    testModifier = (testStatsState.value.testModifier + 1) % TestStatModifiers.size
                )
            }
            is TestStatsStateEvents.testMinimize -> {
                testStatsState.value = testStatsState.value.copy(
                    minimized = (testStatsState.value.minimized + 1) % MinMaxModifier.size
                )
            }
            is TestStatsStateEvents.testMaximize -> {
                testStatsState.value = testStatsState.value.copy(
                    maximized = (testStatsState.value.maximized + 1) % MinMaxModifier.size
                )
            }
            else -> {
                Log.v("onTest", "Event is null, screen has started.")
            }
        }

        testStatsState.value =
            testStatsState.value.copy(probabilityLeq =
            calculateTestProbability(
//                baseProbability = probabilitySumLeqOnNDice(
//                    nDice = testStatsState.value.diceNumber,
//                    threshold = testStatsState.value.thresholdIdx +
//                            testStatsState.value.diceNumber ),
                baseProbability = calculateTestBaseProbability(
                    nDice = testStatsState.value.diceNumber,
                    threshold = testStatsState.value.thresholdIdx +
                            testStatsState.value.diceNumber,
                    minimized = testStatsState.value.minimized,
                    maximized = testStatsState.value.maximized,
                    moreThan = false
                ),
                modifier = testStatsState.value.testModifier
            ))
        testStatsState.value =
            testStatsState.value.copy(probabilityGeq =
                calculateTestProbability(
                    baseProbability = calculateTestBaseProbability(
                        nDice = testStatsState.value.diceNumber,
                        threshold = testStatsState.value.thresholdIdx +
                                testStatsState.value.diceNumber,
                        minimized = testStatsState.value.minimized,
                        maximized = testStatsState.value.maximized,
                        moreThan = true
                    ),
                    modifier = testStatsState.value.testModifier
                ))
        if ( TEST_SUM_EQUALS ) // This won't be needed most of the times
            testStatsState.value =
                testStatsState.value.copy(probabilityEq =
                    calculateTestProbability(
                        baseProbability = probabilitySumEqualsOnNDice(
                            nDice = testStatsState.value.diceNumber,
                            threshold = testStatsState.value.thresholdIdx +
                                    testStatsState.value.diceNumber ),
                        modifier = testStatsState.value.testModifier
                    ))
    }

    /* This function is called on screen startup */
    fun onTestStart() : Unit {
        onTest(null)
    }

    /* This function is called on screen startup */
    fun onAttackStart() : Unit {
        onAttack(null)
    }
}

/* State of the attack screen */
data class AttackStatsState(
    var attacksNumber: Int = 1,
    var toHitIdx: Int = 0,
    var toWoundIdx: Int = 0,
    var armourSaveIdx: Int = 5,
    var specialSaveIdx: Int = 5,
    var toHitModifier: Int = 0,
    var toWoundModifier: Int = 0,
    var armourSaveModifier: Int = 0,
    var specialSaveModifier: Int = 0,
    var poisonAttacks : Boolean = false,
    var lethalStrike : Boolean = false,
    var battleFocus : Boolean = false,
    var fortitude : Boolean = false,
    var probability: Double = 1.0,
    var averageAttacks: Double = 1.0
)

/* State of the test screen */
data class TestStatsState(
    var diceNumber: Int = 1,
    var thresholdIdx: Int = 0,
    var testModifier: Int = 0,
    var minimized: Int = 0,
    var maximized: Int = 0,
    var probabilityLeq: Double = 1.0,
    var probabilityGeq: Double = 1.0,
    var probabilityEq: Double = 1.0
)


/* Different events occurring in the attack screen */
sealed class AttackStatsStateEvents {
    data class attacksNumberEntered(val attacksNumber: Int) : AttackStatsStateEvents()
    data class toHitChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class toWoundChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class armourSaveChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class specialSaveChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class toHitModify(val modifier: Int = 1) : AttackStatsStateEvents()
    data class toWoundModify(val modifier: Int = 1) : AttackStatsStateEvents()
    data class armourSaveModify(val modifier: Int = 1) : AttackStatsStateEvents()
    data class specialSaveModify(val modifier: Int = 1) : AttackStatsStateEvents()
    data class specialAttack(val specialAttack: Int) : AttackStatsStateEvents()
}

/* Different events occurring in the test screen */
sealed class TestStatsStateEvents {
    data class diceNumberEntered(val diceNumber: Int) : TestStatsStateEvents()
    data class thresholdChoice(val threshold: Int) : TestStatsStateEvents()
    data class testModify(val modifier: Int = 1) : TestStatsStateEvents()
    data class testMinimize(val instances: Int = 1) : TestStatsStateEvents()
    data class testMaximize(val instances: Int = 1) : TestStatsStateEvents()
}