package com.example.t9ahowtodie.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

const val D6 = 6

const val NORMAL = 0
const val REROLL_FAILED = 1
const val REROLL_SUCCESS = 2
const val REROLL_ONES = 3
const val REROLL_SIXES = 4

class AttackStatsViewModel: ViewModel() {
    val attackStatsState = mutableStateOf( AttackStatsState() )

    fun chancesAttack(idx : Int) : Double {
        return ((D6 - idx).toDouble())
    }

    fun chancesSave(idx : Int) : Double {
        return ((D6 - idx - 1).toDouble())
    }

    fun calculateBaseProbability(chances : Double, modifier : Int = 0) : Double {
        when (modifier) {
            NORMAL -> {
                return (chances / D6)
            }
            else -> {
                Log.wtf("Probability", "This should never happen!")
                return 0.0
            }
        }

    }

    fun calculateProbability() : Double {
        val probability = (
                calculateBaseProbability(chancesAttack(attackStatsState.value.toHitIdx)) *
                calculateBaseProbability(chancesAttack(attackStatsState.value.toWoundIdx)) *
                (1 - calculateBaseProbability(chancesSave(attackStatsState.value.armourSaveIdx))) *
                (1 - calculateBaseProbability(chancesSave(attackStatsState.value.specialSaveIdx)))
                )
        return (probability * 100.0)
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
            else -> {
                Log.wtf("Event", "This should never happen!")
            }
        }
        attackStatsState.value = attackStatsState.value.copy(probability = calculateProbability())
        attackStatsState.value.averageAttacks =
            (attackStatsState.value.probability / 100.0 * attackStatsState.value.attacksNumber)
    }
}

data class AttackStatsState(
    var attacksNumber: Int = 1,
    var toHitIdx: Int = 0,
    var toWoundIdx: Int = 0,
    var armourSaveIdx: Int = 5,
    var specialSaveIdx: Int = 5,
    var probability: Double = 100.0,
    var averageAttacks: Double = 0.0
)

sealed class AttackStatsStateEvents {
    data class attacksNumberEntered(val attacksNumber: Int) : AttackStatsStateEvents()
    data class toHitChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class toWoundChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class armourSaveChoice(val diceValue: Int) : AttackStatsStateEvents()
    data class specialSaveChoice(val diceValue: Int) : AttackStatsStateEvents()
}