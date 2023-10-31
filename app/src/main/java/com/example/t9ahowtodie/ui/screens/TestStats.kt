package com.example.t9ahowtodie.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.t9ahowtodie.ui.StatsViewModel
import com.example.t9ahowtodie.ui.TEST_SUM_EQUALS
import com.example.t9ahowtodie.ui.TestStatModifiers
import com.example.t9ahowtodie.ui.TestStatsStateEvents
import com.example.t9ahowtodie.ui.components.BackgroundImg
import com.example.t9ahowtodie.ui.components.MAX_TEST_DIGITS
import com.example.t9ahowtodie.ui.components.RadioGrid
import com.example.t9ahowtodie.ui.components.StatModifierToggleButton
import com.example.t9ahowtodie.ui.components.TextComponent
import com.example.t9ahowtodie.ui.components.TextFieldNumber
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme

@Composable
fun TestStats(navHostController: NavHostController, viewModel: StatsViewModel) {
    BackgroundImg(blur = true)

    BackHandler {
        // do nothing
    }

    /* Fake LifeCycle Listener for Composables */
    DisposableEffect(
        key1 = viewModel,
        effect = {// What to do when composable gets added
            viewModel.onTestStart()
            this.onDispose { /* do nothing on composable disposed */ }
        })

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        /* Number of Attacks */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            TextComponent(text = "# of Dice", size = 30.sp)
            Spacer(modifier = Modifier.weight(1f))
            TextFieldNumber ( // Call the result update whenever changing the number of dice
                maxDigits = MAX_TEST_DIGITS,
                maxVal = 5,
                textChangedCallback = {
                viewModel.onTest(TestStatsStateEvents.diceNumberEntered(Integer.parseInt(it)))
            })
        }

        val paddingBetweenRows = 15.dp

        /* The generated Threshold */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingBetweenRows, horizontal = 5.dp)
        ) {
            TextComponent(text = "Threshold", size = 20.sp,
                modifier = Modifier.padding(horizontal = 10.dp))
            Spacer(Modifier.weight(1f))
            StatModifierToggleButton(
                statuses = TestStatModifiers,
                currentStatus = viewModel.testStatsState.value.testModifier) {
                viewModel.onTest(TestStatsStateEvents.testModify())
            }
        }
        RadioGrid(
            diceValues = viewModel.numberOfValuesWithDice(
                viewModel.testStatsState.value.diceNumber
            ),
            checkedIndex = viewModel.testStatsState.value.thresholdIdx) {
            viewModel.onTest(TestStatsStateEvents.thresholdChoice(it))
        }

        /* Probabilities */
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            TextComponent(text = "Probability <=", size = 25.sp)
            Spacer(Modifier.weight(1f))
            TextComponent(
                text = String.format("%.2f%%",
                    viewModel.testStatsState.value.probabilityLeq * 100.0),
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
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            TextComponent(text = "Probability >=", size = 25.sp)
            Spacer(Modifier.weight(1f))
            TextComponent(
                text = String.format("%.2f%%",
                    (viewModel.testStatsState.value.probabilityGeq) * 100.0),
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

        if ( TEST_SUM_EQUALS )
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            ) {
                TextComponent(text = "Probability ==", size = 25.sp)
                Spacer(Modifier.weight(1f))
                TextComponent(
                    text = String.format("%.2f%%",
                        (viewModel.testStatsState.value.probabilityEq) * 100.0),
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


    }
}


@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TestsPreview() {
    T9AHowToDieTheme {
        TestStats(rememberNavController(), StatsViewModel())
    }
}