package com.example.t9ahowtodie.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.math.pow

const val D6 = 6

const val NORMAL = 0
const val REROLL_FAILED = 1
const val REROLL_SUCCESS = 2
const val REROLL_ONES = 3
const val REROLL_SIXES = 4
val statModifiers = arrayListOf<String>(
    "Normal",
    "Reroll Failed",
    "Reroll Success",
    "Reroll Ones",
    "Reroll Sixes"
)

class AttackStatsViewModel: ViewModel() {
    val attackStatsState = mutableStateOf( AttackStatsState() )

    private fun chancesAttack(idx : Int) : Double {
        return ((D6 - idx).toDouble())
    }

    private fun chancesSave(idx : Int) : Double {
        return ((D6 - idx - 1).toDouble())
    }

    private fun calculateBaseProbability(chances : Double, modifier : Int = 0) : Double {
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
                ( (chances / D6) + ( ( 1.0 / D6) * (chances / D6) ) )
            }
            REROLL_SIXES -> {
                ( ( (chances - 1.0) / D6) + ( ( 1.0 / D6) * (chances / D6) ) )
            }
            else -> {
                Log.wtf("Probability", "This should never happen!")
                0.0
            }
        }

    }

    private fun calculateProbability(): Double {
        return (
                calculateBaseProbability(
                    chancesAttack(attackStatsState.value.toHitIdx),
                    modifier = attackStatsState.value.toHitModifier
                ) *
                        calculateBaseProbability(
                            chancesAttack(attackStatsState.value.toWoundIdx),
                            modifier = attackStatsState.value.toWoundModifier
                        ) *
                        (1 - calculateBaseProbability(
                            chancesSave(attackStatsState.value.armourSaveIdx),
                            modifier = attackStatsState.value.armourSaveModifier
                        )) *
                        (1 - calculateBaseProbability(
                            chancesSave(attackStatsState.value.specialSaveIdx),
                            modifier = attackStatsState.value.specialSaveModifier
                        ))
                )
    }

    fun onEvent(event: AttackStatsStateEvents) {
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
                    (attackStatsState.value.toHitModifier + 1) % statModifiers.size )
            }
            is AttackStatsStateEvents.toWoundModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(toWoundModifier =
                    (attackStatsState.value.toWoundModifier + 1) % statModifiers.size )
            }
            is AttackStatsStateEvents.armourSaveModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(armourSaveModifier =
                    (attackStatsState.value.armourSaveModifier + 1) % statModifiers.size )
            }
            is AttackStatsStateEvents.specialSaveModify -> {
                attackStatsState.value =
                    attackStatsState.value.copy(specialSaveModifier =
                    (attackStatsState.value.specialSaveModifier + 1) % statModifiers.size )
            }
            else -> {
                Log.wtf("Event", "This should never happen!")
            }
        }
        attackStatsState.value = attackStatsState.value.copy(probability = calculateProbability())
        attackStatsState.value.averageAttacks =
            attackStatsState.value.probability * attackStatsState.value.attacksNumber
    }
}

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
    var probability: Double = 100.0,
    var averageAttacks: Double = 0.0
)

sealed class AttackStatsStateEvents {
    data class attacksNumberEntered(val attacksNumber: Int) : AttackStatsStateEvents()
    data class toHitChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class toWoundChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class armourSaveChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class specialSaveChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class toHitModify(val modifier: Int = 0) : AttackStatsStateEvents()
    data class toWoundModify(val modifier: Int = 0) : AttackStatsStateEvents()
    data class armourSaveModify(val modifier: Int = 0) : AttackStatsStateEvents()
    data class specialSaveModify(val modifier: Int = 0) : AttackStatsStateEvents()
}