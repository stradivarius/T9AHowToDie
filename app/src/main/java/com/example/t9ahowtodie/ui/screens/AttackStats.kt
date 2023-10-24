package com.example.t9ahowtodie.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.t9ahowtodie.ui.AttackStatsStateEvents
import com.example.t9ahowtodie.ui.AttackStatsViewModel
import com.example.t9ahowtodie.ui.components.BackgroundImg
import com.example.t9ahowtodie.ui.components.SixRadioButtons
import com.example.t9ahowtodie.ui.components.TextComponent
import com.example.t9ahowtodie.ui.components.TextFieldNumber
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme

@Composable
fun AttackStats(navHostController: NavHostController, viewModel: AttackStatsViewModel) {
    BackgroundImg(blur = true)

    Column(modifier = Modifier
        .fillMaxSize()) {

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
                textChangedCallback = {
                viewModel.onEvent(AttackStatsStateEvents.attacksNumberEntered(Integer.parseInt(it)))
            })
        }

        /* To Hit */
        TextComponent(text = "To Hit", size = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp))
        SixRadioButtons(arrayListOf("A", "2+", "3+", "4+", "5+", "6"),
            checkedIndex = viewModel.attackStatsState.value.toHitIdx) {
            viewModel.onEvent(AttackStatsStateEvents.toHitChoice(it))
        }

        /* To Wound */
        TextComponent(text = "To Wound", size = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp))
        SixRadioButtons(arrayListOf("A", "2+", "3+", "4+", "5+", "6"),
            checkedIndex = viewModel.attackStatsState.value.toWoundIdx) {
            viewModel.onEvent(AttackStatsStateEvents.toWoundChoice(it))
        }

        /* Armour Save */
        TextComponent(text = "Armour Save", size = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp))
        SixRadioButtons(arrayListOf("2+", "3+", "4+", "5+", "6", "N/A"),
            checkedIndex = viewModel.attackStatsState.value.armourSaveIdx) {
            viewModel.onEvent(AttackStatsStateEvents.armourSaveChoice(it))
        }

        /* Special Save */
        TextComponent(text = "Special Save", size = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp))
        SixRadioButtons(arrayListOf("2+", "3+", "4+", "5+", "6", "N/A"),
            checkedIndex = viewModel.attackStatsState.value.specialSaveIdx) {
            viewModel.onEvent(AttackStatsStateEvents.specialSaveChoice(it))
        }

        /* Probability */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            TextComponent(text = "Probability", size = 25.sp)
            Spacer(Modifier.weight(1f))
            TextComponent(
                text = String.format("%.2f%%", viewModel.attackStatsState.value.probability),
                size = 25.sp,
                modifier = Modifier.border(width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape)
                    .padding(10.dp)
            )
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
                modifier = Modifier.border(width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape)
                    .padding(10.dp)
            )
        }



    }
}


@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun AttacksPreview() {
    T9AHowToDieTheme {
        AttackStats(rememberNavController(), AttackStatsViewModel())
    }
}