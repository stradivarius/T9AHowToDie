package com.example.t9ahowtodie.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.t9ahowtodie.ui.AttackStatsStateEvents
import com.example.t9ahowtodie.ui.StatsViewModel
import com.example.t9ahowtodie.ui.components.BackgroundImg
import com.example.t9ahowtodie.ui.components.MAX_ATTACKS_DIGITS
import com.example.t9ahowtodie.ui.components.SixRadioButtons
import com.example.t9ahowtodie.ui.components.StatModifierToggleButton
import com.example.t9ahowtodie.ui.components.TextComponent
import com.example.t9ahowtodie.ui.components.TextFieldNumber
import com.example.t9ahowtodie.ui.AttackStatModifiers
import com.example.t9ahowtodie.ui.BATTLE_FOCUS
import com.example.t9ahowtodie.ui.FORTITUDE
import com.example.t9ahowtodie.ui.LETHAL
import com.example.t9ahowtodie.ui.POISON
import com.example.t9ahowtodie.ui.components.OnOffToggleButton
import com.example.t9ahowtodie.ui.components.SmallRadioButton
import com.example.t9ahowtodie.ui.components.StatRadioButton
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme

@Composable
fun AttackStats(navHostController: NavHostController, viewModel: StatsViewModel) {
    BackgroundImg(blur = true)

    BackHandler {
        // do nothing
    }



    /* Fake LifeCycle Listener for Composables */
    DisposableEffect(
        key1 = viewModel,
        effect = {// What to do when composable gets added
            viewModel.onAttackStart()
            this.onDispose { /* do nothing on composable disposed */ }
        })

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {

        /* Number of Attacks */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            TextComponent(text = "# of Attacks", size = 30.sp)
            Spacer(modifier = Modifier.weight(1f))
            TextFieldNumber ( // Call the result update whenever changing the number of attacks
                maxDigits = MAX_ATTACKS_DIGITS,
                textChangedCallback = {
                viewModel.onAttack(
                    AttackStatsStateEvents.attacksNumberEntered(Integer.parseInt(it))
                )
            })
        }

        val paddingBetweenRows = 15.dp

        /* To Hit */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingBetweenRows, horizontal = 5.dp)
        ) {
            TextComponent(text = "To Hit", size = 20.sp,
                modifier = Modifier.padding(horizontal = 10.dp))
            Spacer(modifier = Modifier.weight(1f))
//            SmallRadioButton(text = "P", checked = viewModel.attackStatsState.value.poisonAttacks) {
//                viewModel.onAttack(AttackStatsStateEvents.specialAttack(POISON))
//            }
//            SmallRadioButton(text = "B", checked = viewModel.attackStatsState.value.battleFocus) {
//                viewModel.onAttack(AttackStatsStateEvents.specialAttack(BATTLE_FOCUS))
//            }
            StatModifierToggleButton(
                statuses = AttackStatModifiers,
                currentStatus = viewModel.attackStatsState.value.toHitModifier) {
                viewModel.onAttack(AttackStatsStateEvents.toHitModify())
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ){
            Spacer(modifier = Modifier.weight(1f))
            OnOffToggleButton(text = "Poison",
                checked = viewModel.attackStatsState.value.poisonAttacks) {
                viewModel.onAttack(AttackStatsStateEvents.specialAttack(POISON))
            }
            Spacer(modifier = Modifier.width(10.dp))
            OnOffToggleButton(text = "Battle Focus",
                checked = viewModel.attackStatsState.value.battleFocus) {
                viewModel.onAttack(AttackStatsStateEvents.specialAttack(BATTLE_FOCUS))
            }
        }
        SixRadioButtons(arrayListOf("A", "2+", "3+", "4+", "5+", "6"),
            checkedIndex = viewModel.attackStatsState.value.toHitIdx) {
            viewModel.onAttack(AttackStatsStateEvents.toHitChoice(it))
        }

        /* To Wound */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingBetweenRows, horizontal = 5.dp)
        ) {
            TextComponent(text = "To Wound", size = 20.sp,
                modifier = Modifier.padding(horizontal = 10.dp))
            Spacer(modifier = Modifier.weight(1f))
//            SmallRadioButton(text = "L", checked = viewModel.attackStatsState.value.lethalStrike) {
//                viewModel.onAttack(AttackStatsStateEvents.specialAttack(LETHAL))
//            }
            StatModifierToggleButton(
                statuses = AttackStatModifiers,
                currentStatus = viewModel.attackStatsState.value.toWoundModifier) {
                viewModel.onAttack(AttackStatsStateEvents.toWoundModify())
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ){
            Spacer(modifier = Modifier.weight(1f))
            OnOffToggleButton(text = "Lethal",
                checked = viewModel.attackStatsState.value.lethalStrike) {
                viewModel.onAttack(AttackStatsStateEvents.specialAttack(LETHAL))
            }
        }
        SixRadioButtons(arrayListOf("A", "2+", "3+", "4+", "5+", "6"),
            checkedIndex = viewModel.attackStatsState.value.toWoundIdx) {
            viewModel.onAttack(AttackStatsStateEvents.toWoundChoice(it))
        }


        /* Armour Save */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingBetweenRows, horizontal = 5.dp)
        ) {
            TextComponent(text = "Armour Save", size = 20.sp,
                modifier = Modifier.padding(horizontal = 10.dp))
            Spacer(modifier = Modifier.weight(1f))
            StatModifierToggleButton(
                statuses = AttackStatModifiers,
                currentStatus = viewModel.attackStatsState.value.armourSaveModifier) {
                viewModel.onAttack(AttackStatsStateEvents.armourSaveModify())
            }
        }
        SixRadioButtons(arrayListOf("2+", "3+", "4+", "5+", "6", "N/A"),
            checkedIndex = viewModel.attackStatsState.value.armourSaveIdx) {
            viewModel.onAttack(AttackStatsStateEvents.armourSaveChoice(it))
        }

        /* Special Save */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingBetweenRows, horizontal = 5.dp)
        ) {
            TextComponent(text = "Special Save", size = 20.sp,
                modifier = Modifier.padding(horizontal = 10.dp))
            Spacer(modifier = Modifier.weight(1f))
//            SmallRadioButton(text = "F", checked = viewModel.attackStatsState.value.fortitude) {
//                viewModel.onAttack(AttackStatsStateEvents.specialAttack(FORTITUDE))
//            }
            StatModifierToggleButton(
                statuses = AttackStatModifiers,
                currentStatus = viewModel.attackStatsState.value.specialSaveModifier) {
                viewModel.onAttack(AttackStatsStateEvents.specialSaveModify())
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ){
            Spacer(modifier = Modifier.weight(1f))
            OnOffToggleButton(text = "Fortitude",
                checked = viewModel.attackStatsState.value.fortitude) {
                viewModel.onAttack(AttackStatsStateEvents.specialAttack(FORTITUDE))
            }
        }
        SixRadioButtons(arrayListOf("2+", "3+", "4+", "5+", "6", "N/A"),
            checkedIndex = viewModel.attackStatsState.value.specialSaveIdx) {
            viewModel.onAttack(AttackStatsStateEvents.specialSaveChoice(it))
        }

        /* Average Wounds */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            TextComponent(text = "Average Wounds", size = 25.sp)
            Spacer(Modifier.weight(1f))
            TextComponent(
                text = String.format("%.2f", viewModel.attackStatsState.value.averageAttacks),
                size = 25.sp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
                    .padding(10.dp)
            )
        }

        /* Probability */
//        Row (
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(all = 10.dp)
//        ) {
//            TextComponent(text = "Probability", size = 25.sp)
//            Spacer(Modifier.weight(1f))
//            TextComponent(
//                text = String.format("%.2f%%", viewModel.attackStatsState.value.probability * 100),
//                size = 25.sp,
//                modifier = Modifier
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.secondary,
//                        shape = CircleShape
//                    )
//                    .padding(10.dp)
//            )
//        }
    }
}


@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun AttacksPreview() {
    T9AHowToDieTheme {
        AttackStats(rememberNavController(), StatsViewModel())
    }
}