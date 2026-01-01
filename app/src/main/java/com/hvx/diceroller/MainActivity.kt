package com.hvx.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hvx.diceroller.ui.theme.DiceRollerTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollerTheme {
                DiceApp()
            }
        }
    }
}

@Composable
fun DiceApp() {
    val context = LocalContext.current
    // Simple check - if context is not an Activity, then it's a preview
    val isPreview = !isRealAndroidContext(context)

    if (isPreview) {
        DiceRollerApp()
    } else {
        RealDiceRollerApp(context)
    }
}

@Composable
fun RealDiceRollerApp(context: android.content.Context) {
    // States for first and second dice - using mutableIntStateOf for better performance
    var dice1Result by remember { mutableIntStateOf(1) }
    var dice2Result by remember { mutableIntStateOf(1) }
    var twoDiceMode by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(false) }

    val rollHistory = remember { mutableStateListOf<String>() }

    // Use TtsManager instead of directly creating TextToSpeech
    val ttsManager = remember {
        TtsManager(context)
    }

    // Clean up TTS resources when composition is destroyed
    DisposableEffect(ttsManager) {
        onDispose {
            ttsManager.cleanup()
        }
    }

    fun rollDice() {
        if (twoDiceMode) {
            val newResult1 = (1..6).random()
            val newResult2 = (1..6).random()
            dice1Result = newResult1
            dice2Result = newResult2

            val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            rollHistory.add(0, "[$timeStamp] roll results: $newResult1 + $newResult2 = ${newResult1 + newResult2}")

            // Use TtsManager for voice output
            ttsManager.speakResult(voiceEnabled, newResult1, newResult2, isTwoDice = true)
        } else {
            val newResult = (1..6).random()
            dice1Result = newResult

            val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            rollHistory.add(0, "[$timeStamp] roll result: $newResult")

            // Use TtsManager for voice output
            ttsManager.speakResult(voiceEnabled, newResult, null, isTwoDice = false)
        }

        if (rollHistory.size > 10) {
            rollHistory.removeAt(rollHistory.size - 1)
        }
    }

    // Function to get dice image resource
    fun getDiceImageResource(result: Int): Int {
        return when(result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }

    DiceUI(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        dice1Result = dice1Result,
        dice2Result = dice2Result,
        twoDiceMode = twoDiceMode,
        voiceEnabled = voiceEnabled,
        rollHistory = rollHistory,
        onRollDice = { rollDice() },
        onTwoDiceModeChange = {
            twoDiceMode = it
        },
        onVoiceEnabledChange = { voiceEnabled = it },
        getDiceImageResource = ::getDiceImageResource,
        isPreview = false
    )
}

@Composable
fun DiceRollerApp() {
    // States for first and second dice - using mutableIntStateOf for better performance
    var dice1Result by remember { mutableIntStateOf(1) }
    var dice2Result by remember { mutableIntStateOf(1) }
    var twoDiceMode by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(false) }

    val rollHistory = remember { mutableStateListOf<String>() }

    fun getDiceImageResource(result: Int): Int {
        return when(result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }

    fun rollDice() {
        if (twoDiceMode) {
            val newResult1 = (1..6).random()
            val newResult2 = (1..6).random()
            dice1Result = newResult1
            dice2Result = newResult2

            val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            rollHistory.add(0, "[$timeStamp] roll results: $newResult1 + $newResult2 = ${newResult1 + newResult2}")
        } else {
            val newResult = (1..6).random()
            dice1Result = newResult

            val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            rollHistory.add(0, "[$timeStamp] roll result: $newResult")
        }

        if (rollHistory.size > 10) {
            rollHistory.removeAt(rollHistory.size - 1)
        }
    }

    DiceUI(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        dice1Result = dice1Result,
        dice2Result = dice2Result,
        twoDiceMode = twoDiceMode,
        voiceEnabled = voiceEnabled,
        rollHistory = rollHistory,
        onRollDice = { rollDice() },
        onTwoDiceModeChange = {
            twoDiceMode = it
            // Remove unused assignments since dice values will be updated on next roll
            // instead of resetting to 1 when mode changes
        },
        onVoiceEnabledChange = { voiceEnabled = it },
        getDiceImageResource = ::getDiceImageResource,
        isPreview = true
    )
}

// Preview function
@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    DiceRollerTheme {
        DiceRollerApp()
    }
}

@Composable
fun DiceUI(
    modifier: Modifier = Modifier,
    dice1Result: Int,
    dice2Result: Int,
    twoDiceMode: Boolean,
    voiceEnabled: Boolean,
    rollHistory: List<String>,
    onRollDice: () -> Unit,
    onTwoDiceModeChange: (Boolean) -> Unit,
    onVoiceEnabledChange: (Boolean) -> Unit,
    getDiceImageResource: (Int) -> Int,
    isPreview: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display dice
        if (twoDiceMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(getDiceImageResource(dice1Result)),
                    contentDescription = "First dice: $dice1Result",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(getDiceImageResource(dice2Result)),
                    contentDescription = "Second dice: $dice2Result",
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Image(
                painter = painterResource(getDiceImageResource(dice1Result)),
                contentDescription = dice1Result.toString()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Roll button
        Button(
            onClick = onRollDice,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Roll!", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Log title
        Text(
            text = "Roll History:",
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Roll log
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if (rollHistory.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    reverseLayout = false
                ) {
                    itemsIndexed(rollHistory) { index, logEntry ->
                        Text(
                            text = logEntry,
                            fontSize = 14.sp,
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No rolls yet. Press 'Roll!' to start!",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox for two dice mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = twoDiceMode,
                onCheckedChange = onTwoDiceModeChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Two dice mode",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Checkbox for voice output
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = voiceEnabled,
                onCheckedChange = onVoiceEnabledChange,
                enabled = !isPreview // Disable only in preview
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPreview) "Voice result (not available in preview)" else "Voice result",
                fontSize = 18.sp,
                color = if (isPreview) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Function to check if context is a real Android context
private fun isRealAndroidContext(context: android.content.Context): Boolean {
    val className = context.javaClass.name
    // Check if context is not a preview
    return !className.contains("Preview", ignoreCase = true) &&
            !className.contains("ComposeView", ignoreCase = true)
}