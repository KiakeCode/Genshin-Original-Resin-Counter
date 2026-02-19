package com.example.genshin_original_resin_counter

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.genshin_original_resin_counter.`class`.NotificationTimer
import com.example.genshin_original_resin_counter.ui.theme.GenshinOriginalResinCounterTheme
import com.example.genshin_original_resin_counter.ui.theme.blue
import com.example.genshin_original_resin_counter.util.MessagesInterface
import com.example.genshin_original_resin_counter.util.calculateResinToAdd
import com.example.genshin_original_resin_counter.util.convertResinInTimeLeftMillis
import com.example.genshin_original_resin_counter.util.formatTimeToString
import com.example.genshin_original_resin_counter.util.validateInput
import com.example.genshin_original_resin_counter.util.validateInputFromDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO Make number o resin more in the sight on the page

val Context.dataStore by preferencesDataStore(name = "counters")

val RESIN = stringPreferencesKey("RESIN")
val TIME_SAVED = longPreferencesKey(name = "TIME_SAVED")

suspend fun saveResin(context: Context, value: String) {
    context.dataStore.edit { preferences ->
        preferences[RESIN] = value
        preferences[TIME_SAVED] = System.currentTimeMillis()
    }
}


suspend fun readResin(context: Context): Flow<String> =
    context.dataStore.data.map { preferences -> preferences[RESIN] ?: "0" }

suspend fun readTimeSaved(context: Context): Flow<Long> =
    context.dataStore.data.map { preferences ->
        preferences[TIME_SAVED] ?: System.currentTimeMillis()
    }


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        checkNotificationPermissionAtFirstStart()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(LocalContext.current)
        }
    }

    private fun checkNotificationPermissionAtFirstStart() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(current: Context) {
//    val borderprop = BorderStroke(1.dp, Color.Red)

    val textsize = 17f
    val focusManager = LocalFocusManager.current

    var totalMillis by remember {
        mutableLongStateOf(0L)
    }
    var millisCounter by remember {
        mutableLongStateOf(totalMillis)
    }
    var input by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }



    LaunchedEffect(key1 = totalMillis) {

        while (millisCounter > 0) {
            millisCounter -= 1000L
            if (millisCounter % (8L * 60L * 1000L) == 0L) {
                Log.d("add+1 resin","added resin")
                try {
                    input = validateInput(input, (input.toInt() + 1).toString())
                    scope.launch {
                        saveResin(current, input)
                    }

                } catch (e: Error) {
                    input = "0"
                    Log.d("Err", e.toString())
                }
            }
            Log.d("time", millisCounter.toString())
            delay(1000L)
        }
        if (millisCounter == 0L && input.isNotEmpty() && input.toInt() == 200) {
            snackbarHostState.showSnackbar(
                MessagesInterface.NOTIFICATION_FULL, withDismissAction = true
            )
            NotificationTimer().showNotification(
                current = current, message = MessagesInterface.NOTIFICATION_FULL
            )
        }
    }

    LaunchedEffect(Unit) {
        Log.d("here", "here")
        combine(
            readTimeSaved(current), readResin(current)
        ) { oldTime, resin ->
            oldTime to resin
        }.collect { (oldTime, resin) ->

            // Setting resins
            input = validateInputFromDataStore(
                input, calculateResinToAdd(
                    oldTime = oldTime,
                    currentTime = System.currentTimeMillis(),
                    resin = resin,
                )
            )

            // Setting time on screen
            val convertedTime = convertResinInTimeLeftMillis(
                resin = mutableStateOf(value = input)
            )
            val timeDifference = System.currentTimeMillis() - oldTime
            val totalMillisToRound =
                convertedTime - if (input.toInt() != 200) (timeDifference) else 0
            totalMillis = totalMillisToRound - (totalMillisToRound % 1000)
            millisCounter = totalMillis
        }
    }



    @Composable
    fun UniversalTextSize(textSize: Float = textsize): TextUnit = TextUnit(
        value = textSize, type = TextUnitType.Sp
    )

    @Composable
    fun UniversalTextStyleBold(): TextStyle = TextStyle(
        fontWeight = FontWeight.W900,
        fontSize = UniversalTextSize(),
        color = blue,
        fontFamily = FontFamily(Font(R.font.zhcn))
    )
//86025767
//86025000

    GenshinOriginalResinCounterTheme {

        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,

                            ) {

                            GenshinOriginalResinCounterTheme {

                                Text(
                                    text = "Original Resin Numbers: ",
                                    fontSize = UniversalTextSize(),
                                    fontWeight = FontWeight.W600
                                )
                                BasicTextField(
                                    value = input,
                                    onValueChange = {
                                        input = validateInput(input, it)
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Next
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier.width(IntrinsicSize.Min),
                                    keyboardActions = KeyboardActions(onNext = {


                                        // Checking Empty Input
                                        if (input.isEmpty()) input = "0"

                                        // The timer updates
                                        totalMillis = convertResinInTimeLeftMillis(
                                            resin = mutableStateOf(value = input)
                                        )
                                        millisCounter = totalMillis

                                        // Saving Resin and Timestamp
                                        scope.launch { saveResin(current, input) }

                                        // Removing Focus from the BasicTextField
                                        focusManager.clearFocus(force = true)
                                    }),
                                    textStyle = UniversalTextStyleBold()
                                )
                                Text(
                                    text = "/200",
                                    fontSize = UniversalTextSize(),
                                    fontWeight = FontWeight.W600
                                )
                            }

                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                )

            },
        ) { e ->
            Column(
                Modifier
                    .padding(paddingValues = e)
                    .fillMaxSize()
                    .clickable(interactionSource = null, indication = null, onClick = {
                        focusManager.clearFocus()
                    }), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatTimeToString(millisCounter).value,
                    fontSize = UniversalTextSize(textsize + 4f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.size(120.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Tap the ")

                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.W900,
                                fontFamily = FontFamily(Font(R.font.zhcn)),
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("resin number")
                        }

                        append(" to update the timer!")
                    },
                    fontSize = UniversalTextSize(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()

                )
            }
        }
    }
}






